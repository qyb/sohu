//
//  SystemTool.m
//  sohukan
//
//  Created by  on 12-2-25.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "SystemTool.h"

@implementation SystemTool

+ (void) reachabilityChanged:(NSNotification *)note {
    Reachability* curReach = [note object];
    NSParameterAssert([curReach isKindOfClass: [Reachability class]]);
    NetworkStatus status = [curReach currentReachabilityStatus];
    
    if (status == NotReachable) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"sohukan"
                                                        message:@"NotReachable"
                                                       delegate:nil
                                              cancelButtonTitle:@"YES" otherButtonTitles:nil];
        [alert show];
        [alert release];
    }
}

+ (BOOL) isEnableWIFI {
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] != NotReachable);
}

+ (BOOL) isEnable3G {
    return ([[Reachability reachabilityForInternetConnection] currentReachabilityStatus] != NotReachable);
}


+ (NSMutableArray *) parseXML:(NSData *)xmlData elementName:(NSString *)elementName attributeName:(NSString *)attributeName;
{
    NSMutableArray *res = [[[NSMutableArray alloc] init] autorelease];
    CXMLDocument *doc = [[[CXMLDocument alloc] initWithData:xmlData options:0 error:nil] autorelease];
    NSArray *nodes = NULL;
    nodes = [doc nodesForXPath:elementName error:nil];
    for (CXMLElement *node in nodes) {
        NSMutableDictionary *item = [[NSMutableDictionary alloc] init];
        int counter;
        for(counter = 0; counter < [node childCount]; counter++) {
            [item setObject:[[node childAtIndex:counter] stringValue] forKey:[[node childAtIndex:counter] name]];
        }
        [item setObject:[[node attributeForName:attributeName] stringValue] forKey:attributeName];
        [res addObject:item];
        [item release];
    }
    return res;
}

+ (NSData *) getDataWithSynchronous:(NSString *)urlString
{
    NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:urlString]
                                             cachePolicy:NSURLRequestUseProtocolCachePolicy
                                         timeoutInterval: 30.0];
    NSURLResponse *response;
    NSError *err;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
    return responseData;
}

+ (BOOL) writeApplicationData:(NSData *)data toFile:(NSString *)fileName
{    
    NSString *path = [self getUserPathForFile:fileName];
    
    return ([data writeToFile:path atomically:YES]);
}


+ (NSString *) getUserPathForFile:(NSString *)fileName {
    NSString *userDirectory = [self getUserDirectory];
    return [userDirectory stringByAppendingPathComponent:fileName];
}

+ (NSString *) getUserDirectory
{
    NSInteger userid = [[NSUserDefaults standardUserDefaults] integerForKey:@"userid"];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *userDirectory = [documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"user_%d",userid]];
    if (!userDirectory) {
        NSLog(@"Documents directory not found!");        
        return documentsDirectory;      
    }else{
        return userDirectory;
    }
    
}

+ (BOOL) createFileDirectory:(NSString*)name
{
    NSFileManager* fileManager = [NSFileManager defaultManager];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask,YES);
    NSString *documentsDirectory = [[paths objectAtIndex:0] stringByAppendingPathComponent:name];
    BOOL isDirectory;
    if([fileManager fileExistsAtPath:documentsDirectory isDirectory:&isDirectory]) {
        return isDirectory;
    }
    [fileManager createDirectoryAtPath:documentsDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    return YES ;
}

+ (UITableViewCell *) setCellStyle:(UITableViewCell *)cell detailLink:(NSString *)urlString;
{
    cell.textLabel.backgroundColor = [UIColor clearColor];
    cell.textLabel.shadowColor = [UIColor whiteColor];
    cell.textLabel.shadowOffset = CGSizeMake(0.0, 1.0);
    NSURL *link = [[NSURL alloc] initWithString:urlString];
    cell.detailTextLabel.text = link.host;
    cell.detailTextLabel.backgroundColor = [UIColor clearColor];
    cell.detailTextLabel.shadowColor = [UIColor whiteColor];
    cell.detailTextLabel.shadowOffset = CGSizeMake(0.0, 1.0);
    [link release];
    return cell;
}

+ (BOOL) processUpdateArticleData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp
{   
    BOOL needDownload = NO;
    FMResultSet *rs = [dp getRowEntity:@"Article" primaryKey:[element objectForKey:@"key"]];
    if([rs next]){
        if ([[element objectForKey:@"is_delete"] intValue]){
            [dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key=%@", [element objectForKey:@"key"]]];
            [dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Image WHERE key=%@", [element objectForKey:@"key"]]];
        }else{ 
            if (![rs boolForColumn:@"is_download"]){
                needDownload = YES;
            }
            if ([rs stringForColumn:@"category"] != [element objectForKey:@"category"]){
                [dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET category='%@' WHERE key=%@", [element objectForKey:@"category"], [element objectForKey:@"key"]]];
            }
            if ([rs stringForColumn:@"title"] != [element objectForKey:@"title"]){
                [dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET title='%@' WHERE key=%@", [element objectForKey:@"title"], [element objectForKey:@"key"]]];
            }
            if ([rs boolForColumn:@"is_read"] != [[element objectForKey:@"is_read"] boolValue]){
                [dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET is_read=%@ WHERE key=%@", [element objectForKey:@"is_read"], [element objectForKey:@"key"]]];
            }
        }
    }
    return needDownload;
}
    
+ (BOOL) processNewArticleData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp userId:(int)userid
{
    BOOL needDownload = NO;    
    if (![[element objectForKey:@"is_delete"] intValue]){
        [dp insertArticleData:element userId:userid];
        if ([element objectForKey:@"category"]) {
            [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", [element objectForKey:@"category"]]];
        }
        if([element objectForKey:@"download_url"]){
            needDownload = YES;
        }
    }
    return needDownload;
}

/*
+ (BOOL)processImageData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp
{
    NSString *img_urls = [NSString stringWithString:[element objectForKey:@"image_urls"]];
    NSMutableArray *urls = [[NSMutableArray alloc] initWithArray:[img_urls componentsSeparatedByString:@"|"]];
    for(NSString *url in urls){
                NSString *link = [url stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                if([link length] >= 1){
                    [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', 0)", [element objectForKey:@"key"], link]];
                    [self startDownload:url key:[[url componentsSeparatedByString:@"/"] lastObject] downloadType:@"image"];
                }
            }
            [urls release];
        }
    }
    return needDownload;
}*/

+ (void) processCagegoryData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp
{
    FMResultSet *rs = [dp getRowEntity:@"Category" primaryKey:[element objectForKey:@"category"]];
    if (![rs next]){
        [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", [element objectForKey:@"category"]]];
    }
}

+ (CATransition *)swipAnimation:(NSString *)direction delegateObject:(id)delegate
{
    CATransition *animation = [CATransition animation];
    [animation setDelegate:delegate];
    [animation setType:kCATransitionPush];
    [animation setSubtype:direction];
    [animation setDuration:0.5f];
    [animation setTimingFunction:[CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut]];
    return animation;
}

+ (BOOL) deleteArticleAndImage:(DatabaseProcess *)dp articleKey:(NSString *)key
{
    NSFileManager *fileManager =[NSFileManager defaultManager];
    [dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key='%@'", key]];
    [fileManager removeItemAtPath:[SystemTool getUserPathForFile:key] error:nil];
    NSMutableArray *imagesURL = [dp getAllImage:key];
    for (NSString *urlString in imagesURL){
        [fileManager removeItemAtPath:[SystemTool getUserPathForFile:urlString] error:nil];
    }
    return YES;

}

+ (NSString *)md5:(NSString *)orignalString  
{  
    const char *cStr = [orignalString UTF8String];  
    unsigned char result[CC_MD5_DIGEST_LENGTH];  
    CC_MD5(cStr, strlen(cStr), result);  
    return [NSString stringWithFormat:  
            @"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",  
            result[0], result[1], result[2], result[3],   
            result[4], result[5], result[6], result[7],  
            result[8], result[9], result[10], result[11],  
            result[12], result[13], result[14], result[15]  
            ];  
}
@end

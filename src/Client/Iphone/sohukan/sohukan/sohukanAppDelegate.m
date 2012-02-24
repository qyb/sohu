//
//  sohukanAppDelegate.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "sohukanAppDelegate.h"
#import "Reachability.h"
#import "DownloadOperation.h"

@implementation sohukanAppDelegate
@synthesize window=_window;
@synthesize navigationController=_navigationController;

- (void)reachabilityChanged:(NSNotification *)note {
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

- (BOOL) isEnableWIFI {
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] != NotReachable);
}

- (BOOL) isEnable3G {
    return ([[Reachability reachabilityForInternetConnection] currentReachabilityStatus] != NotReachable);
}

-(NSMutableArray *)parseXML:(NSData *)xmlData path:(NSString *)pname attributeName:(NSString *)aname
{
    NSMutableArray *res = [[[NSMutableArray alloc] init] autorelease];
    CXMLDocument *doc = [[[CXMLDocument alloc] initWithData:xmlData options:0 error:nil] autorelease];
    NSArray *nodes = NULL;
    nodes = [doc nodesForXPath:pname error:nil];
    for (CXMLElement *node in nodes) {
        NSMutableDictionary *item = [[NSMutableDictionary alloc] init];
        int counter;
        for(counter = 0; counter < [node childCount]; counter++) {
            [item setObject:[[node childAtIndex:counter] stringValue] forKey:[[node childAtIndex:counter] name]];
        }
        [item setObject:[[node attributeForName:aname] stringValue] forKey:aname];
        [res addObject:item];
        [item release];
    }
    return res;
}


- (BOOL)writeApplicationData:(NSData *)data toFile:(NSString *)fileName {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    if (!documentsDirectory) {
        NSLog(@"Documents directory not found!");        
        return NO;        
    }    
    NSString *appFile = [documentsDirectory stringByAppendingPathComponent:fileName];
    return ([data writeToFile:appFile atomically:YES]);
    
}

- (void)observeValueForKeyPath:(NSString*)keyPath ofObject:(id)object
                        change:(NSDictionary*)change context:(void*)context
{
    [object removeObserver:self forKeyPath:keyPath];
    [self writeApplicationData:((DownloadOperation*)object).data toFile:((DownloadOperation*)object).key];
    /*NSString *sql = nil;
    if ([@"html" isEqualToString:((DownloadOperation*)object).type]){
        NSLog(@"DOWLOAD html,%@",((DownloadOperation*)object).key);
        sql = [NSString stringWithFormat:@"UPDATE Article SET is_download=1 WHERE key='%@'",((DownloadOperation*)object).key];
        [_dp executeUpdate:sql];
        NSLog(@"write db html");
    }else{
        NSLog(@"DOWLOAD image,%@",((DownloadOperation*)object).key);
        sql = [NSString stringWithFormat:@"UPDATE Image SET is_download=1 WHERE key='%@'",((DownloadOperation*)object).key];
        [_dp executeUpdate:sql];
        NSLog(@"write db image");
    }*/
}

-(void)startDownload:(NSString *)url key:(NSString *)key downloadType:(NSString *)type
{
    NSURLRequest*  request = [NSURLRequest requestWithURL:[NSURL URLWithString:url]
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                                              timeoutInterval: 30.0];
    DownloadOperation*  operation = [[DownloadOperation alloc] initWithRequest:request];
    operation.key = key;
    operation.type = type;
    [operation autorelease];
    [operation addObserver:self forKeyPath:@"isFinished"
                   options:NSKeyValueObservingOptionNew context:nil];
    [_queue addOperation:operation];

}

-(NSData *)getDataWithSynchronous:(NSString *)url
{
    NSURLRequest *request = [NSURLRequest requestWithURL:
                                [NSURL URLWithString:url]
                                        cachePolicy:NSURLRequestUseProtocolCachePolicy
                                        timeoutInterval: 30.0];
    NSURLResponse *response;
    NSError *err;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
    return responseData;
}

-(void)processArticleData:(NSMutableDictionary *)element
{   
    FMResultSet *rs = [_dp getRowEntity:@"Article" primaryKey:[element objectForKey:@"key"]];
    if([rs next]){
        if ([[element objectForKey:@"is_delete"] intValue]){
            [_dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key=%@", [element objectForKey:@"key"]]];
            [_dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Image WHERE key=%@", [element objectForKey:@"key"]]];
        }else{ 
            if (![rs boolForColumn:@"is_download"]){
            [self startDownload:[rs stringForColumn:@"download_url"] key:[rs stringForColumn:@"key"] downloadType:@"html"];
            }
            if ([rs stringForColumn:@"category"] != [element objectForKey:@"category"]){
                [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET category='%@' WHERE key=%@", [element objectForKey:@"category"], [element objectForKey:@"key"]]];
            }
            if ([rs stringForColumn:@"title"] != [element objectForKey:@"category"]){
                [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET title='%@' WHERE key=%@", [element objectForKey:@"title"], [element objectForKey:@"key"]]];
            }
            if ([rs boolForColumn:@"is_read"] != [[element objectForKey:@"is_read"] boolValue]){
                [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET is_read=%@ WHERE key=%@", [element objectForKey:@"is_read"], [element objectForKey:@"key"]]];
            }
        }
    }else{
        if (![[element objectForKey:@"is_delete"] intValue]){
            [_dp insertArticleData:element];
            if ([element objectForKey:@"category"]) {
                [_dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", [element objectForKey:@"category"]]];
            }
            if([element objectForKey:@"download_url"]){
                [self startDownload:[element objectForKey:@"download_url"]
                        key:[element objectForKey:@"key"] 
                        downloadType:@"html"];
            }
            NSString *img_urls = [NSString stringWithString:[element objectForKey:@"image_urls"]];
            NSMutableArray *urls = [[NSMutableArray alloc] initWithArray:[img_urls componentsSeparatedByString:@"|"]];
            for(NSString *url in urls){
                NSString *link = [url stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                if([link length] >= 1){
                    [_dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', 0)", [element objectForKey:@"key"], link]];
                    [self startDownload:url key:[[url componentsSeparatedByString:@"/"] lastObject] downloadType:@"image"];
                }
            }
            [urls release];
        }
    }
}

-(void)processCagegoryData:(NSMutableDictionary *)element
{
    FMResultSet *rs = [_dp getRowEntity:@"Category" primaryKey:[element objectForKey:@"category"]];
    if (![rs next]){
        [_dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", [element objectForKey:@"category"]]];
    }
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window.rootViewController = self.navigationController;
    _dp = [[DatabaseProcess alloc] init];
    _queue = [[NSOperationQueue alloc] init];
    [self.window makeKeyAndVisible];
    [_dp createVersionTable];
    int version = [_dp getVersionId];
    NSString *_access_token = @"649cfef6a94ee38f0c82a26dc8ad341292c7510e";
    BOOL need_update = NO;
    if ([self isEnableWIFI] || [self isEnable3G]){
        if (version){
            NSString *articleURL = [[NSString alloc] initWithFormat:@"http://10.10.69.53/article/list.xml?access_token=%@&limit=5", _access_token];
            NSData *articleXML = [self getDataWithSynchronous:articleURL];
            NSMutableArray *array = [self parseXML:articleXML path:@"//article" attributeName:@"key"];
            [articleURL release];
            for(NSMutableDictionary *entity in array){
                [self processArticleData:entity];
            }
            /*
            //NSString *recordURL = [[NSString alloc] initWithFormat:@"http://10.10.69.53/record/list.xml?access_token=%@&last_version=%@", @"649cfef6a94ee38f0c82a26dc8ad341292c7510e", version];
            NSData *responseData = [self getDataWithSynchronous:@"http://10.10.69.53/record/list.xml?access_token=649cfef6a94ee38f0c82a26dc8ad341292c7510e&last_version=1"];
            //[recordURL release];
            NSMutableArray *operRecordXML = [self parseXML:responseData path:@"//oper_record" attributeName:@"record_id"];
            for(NSMutableDictionary *element in operRecordXML){
                if([element objectForKey:@"object_type"] == @"article"){
                    NSString *articleURL = [[NSString alloc] initWithFormat:@"http://10.10.69.53/article/show/%@.xml?access_token=%@",[element objectForKey:@"object_key"], _access_token];
                    NSData *articleXML = [self getDataWithSynchronous:articleURL];
                    NSMutableArray *array = [self parseXML:articleXML path:@"//article" attributeName:@"key"];
                    [articleURL release];
                    for(NSMutableDictionary *entity in array){
                        [self processArticleData:entity];
                    }
                }else{
                    NSString *categoryURL = [[NSString alloc] initWithFormat:@"http://10.10.69.53/article/category/list.xml?access_token=%@", _access_token];
                    NSData *categoryXML = [self getDataWithSynchronous:categoryURL];
                    [categoryURL release];
                    NSMutableArray *res = [[NSMutableArray alloc] init];
                    CXMLDocument *doc = [[[CXMLDocument alloc] initWithData:categoryXML options:0 error:nil] autorelease];
                    NSArray *nodes = NULL;
                    nodes = [doc nodesForXPath:@"category" error:nil];
                    for (CXMLElement *node in nodes) {
                        NSMutableDictionary *item = [[NSMutableDictionary alloc] init];
                        int counter;
                        for(counter = 0; counter < [node childCount]; counter++) {
                            [item setObject:[[node childAtIndex:counter] stringValue] forKey:[[node childAtIndex:counter] name]];
                            }
                        [res addObject:item];
                        [item release];
                    }
                    for(NSMutableDictionary *entity in res){
                        [self processCagegoryData:entity];
                    }
                    [res release];
                }
                int new_version = [[element objectForKey:@"record_id"] intValue];
                if (version < new_version)
                {
                    version = new_version;
                    need_update = YES;
                }
            }*/
        }else{
            [_dp createArticleTable];
            [_dp createImageTable];
            [_dp createCategoryTable];
            [_dp createVersionTable];
            [_dp createHistoryTable];
            [_dp createOperationTable];
            NSData *data = [self getDataWithSynchronous:[NSString stringWithFormat:@"http://10.10.69.53/article/list.xml?access_token=%@", _access_token]];
            NSMutableArray *articleXML = [self parseXML:data path:@"//article" attributeName:@"key"];
            for(NSMutableDictionary *element in articleXML){
                [self processArticleData:element];
            }
            [_dp executeUpdate:@"INSERT INTO Version (version) VALUES (1)"];
        }
    }else{
        NSLog(@"No network");
    }
    NSLog(@"Parse xml success");
    //3g:2, edge:1, wifi:6
    //[_queue setMaxConcurrentOperationCount:6];
    if (need_update){
        [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Version SET version=%@", version]];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self
                                          selector:@selector(reachabilityChanged:)
                                          name:@"kReachabilityChangedNotification"
                                          object: nil];
    //[[NSNotificationCenter defaultCenter] postNotificationName:@"kReachabilityChangedNotification" object:nil];
    [_access_token release];
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
}

- (void)applicationWillTerminate:(UIApplication *)application
{
}

- (void)dealloc
{   
    [_window release];
    [_navigationController release];
    [_dp release];
    [super dealloc];
}

@end

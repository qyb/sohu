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
#import "DatabaseProcess.h"

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

+ (BOOL) IsEnableWIFI {
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] != NotReachable);
}

+ (BOOL) IsEnable3G {
    return ([[Reachability reachabilityForInternetConnection] currentReachabilityStatus] != NotReachable);
}

-(NSMutableArray *)parseXML:(NSData *)xmlData
{
    NSMutableArray *res = [[[NSMutableArray alloc] init] autorelease];
    //NSString *XMLPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"test.xml"];
    //NSData *XMLData   = [NSData dataWithContentsOfFile:XMLPath];
    CXMLDocument *doc = [[[CXMLDocument alloc] initWithData:xmlData options:0 error:nil] autorelease];
    NSArray *nodes = NULL;
    nodes = [doc nodesForXPath:@"//article" error:nil];
    for (CXMLElement *node in nodes) {
        NSMutableDictionary *item = [[NSMutableDictionary alloc] init];
        int counter;
        for(counter = 0; counter < [node childCount]; counter++) {
            [item setObject:[[node childAtIndex:counter] stringValue] forKey:[[node childAtIndex:counter] name]];
        }
        [item setObject:[[node attributeForName:@"key"] stringValue] forKey:@"key"];
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
    //DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    //[dp updateArticle:((DownloadOperation*)object).key columnName:@"is_download" setValue:(id)1];
    //[dp closeDB];
    //[dp release];
    //NSLog(@"download ok %@", [((DownloadOperation*)object).data length]);
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


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window.rootViewController = self.navigationController;
    [self.window makeKeyAndVisible];
    NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:@"http://192.168.0.102/article/list.xml/?access_token=875370a11da2b613f7fd883d051770a51a9d2027"]
                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              timeoutInterval: 30.0];
    NSURLResponse *response;
    NSError *err;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
    //NSString *result = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
    //NSLog(@"RESULT:%@", result);
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    if (![dp tableExist:@"Article"]){
        [dp createArticleTable];
    }
    NSMutableArray *xmlData = [[[NSMutableArray alloc] init] autorelease];
    xmlData = [self parseXML:responseData];
    NSLog(@"Parse xml success");
    _queue = [[NSOperationQueue alloc] init];
    //3g:2, edge:1, wifi:6
    //[_queue setMaxConcurrentOperationCount:6];
    for(NSMutableDictionary *element in xmlData)
    {   BOOL is_download;
        if ([dp articleExist:[element objectForKey:@"key"]]){
            [dp updateData:@"Article" primaryKey:[element objectForKey:@"key"] columnName:@"is_read" setValue:[element objectForKey:@"is_read"]];
        }else{
            [dp insertArticleData:element];
        }
        
            if([element objectForKey:@"download_url"]){
                [self startDownload:[element objectForKey:@"download_url"]
                                key:[element objectForKey:@"key"] 
                                downloadType:@"html"];
            }
            NSString *img_urls = [NSString stringWithString:[element objectForKey:@"image_urls"]];
            NSMutableArray *urls = [[NSMutableArray alloc] initWithArray:[img_urls componentsSeparatedByString:@"|"]];
            for(NSString *url in urls){
                if(url){
                    [self startDownload:[element objectForKey:@"download_url"]
                                    key:[element objectForKey:@"key"]
                                    downloadType:@"image"];
                }
            }
            [urls release];
    }
    [dp closeDB];
    [dp release];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                          selector:@selector(reachabilityChanged:)
                                          name:@"kReachabilityChangedNotification"
                                          object: nil];
    
    //[[NSNotificationCenter defaultCenter] postNotificationName:@"kReachabilityChangedNotification" object:nil];
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
    [super dealloc];
}

@end

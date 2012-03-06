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
#import "SystemTool.h"

@implementation sohukanAppDelegate
@synthesize window=_window;
@synthesize navigationController=_navigationController;

- (void)observeValueForKeyPath:(NSString*)keyPath ofObject:(id)object
                        change:(NSDictionary*)change context:(void*)context
{
    [object removeObserver:self forKeyPath:keyPath];
    [SystemTool writeApplicationData:((DownloadOperation*)object).data toFile:((DownloadOperation*)object).key];
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

-(void)showFirstRunView:(BOOL)animated
{
	[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleBlackOpaque animated:YES];
	if(!firstRunView) {
		firstRunView = [[FirstRunViewController alloc] initWithNibName:@"FirstRunViewController" bundle:nil];
		firstRunView.view.frame = [UIScreen mainScreen].applicationFrame;
	}

	if(animated) {
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:_mainView cache:YES];
		[UIView setAnimationDuration:0.4];
	}
	[[_mainView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
	[_mainView addSubview:firstRunView.view];
	if(animated){
		[UIView commitAnimations];
    }
}

-(void)showGuideView:(BOOL)animated
{
	[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleBlackOpaque animated:YES];
	if(!guideView) {
		guideView = [[GuideViewController alloc] init];
		guideView.view.frame = [UIScreen mainScreen].applicationFrame;
	}
    [_mainView addSubview:guideView.view];
    /*
	if(animated) {
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:_mainView cache:YES];
		[UIView setAnimationDuration:0.4];
	}
	[[_mainView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
	[_mainView addSubview:firstRunView.view];
	if(animated){
		[UIView commitAnimations];
    }*/
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // _mainView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
	//[self.window addSubview:_mainView];
    //[self showFirstRunView:YES];
    [self.window makeKeyAndVisible];
    //[self showGuideView:YES];
    
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    _queue = [[NSOperationQueue alloc] init];
    [self.window makeKeyAndVisible];
    NSInteger lastVersion = [[NSUserDefaults standardUserDefaults] integerForKey:@"lastVersion"];
    NSString *username = [[NSUserDefaults standardUserDefaults] stringForKey:@"username"];
    if (!username){
        [[NSUserDefaults standardUserDefaults] setObject:@"hbtest023@sohu.com" forKey:@"username"];
        username = @"hbtest@sohu.com";
    }
    NSString *_access_token = [[NSUserDefaults standardUserDefaults] stringForKey:@"access_token"];
    if (!_access_token) {
        _access_token = @"649cfef6a94ee38f0c82a26dc8ad341292c7510e";
        [[NSUserDefaults standardUserDefaults] setObject:@"649cfef6a94ee38f0c82a26dc8ad341292c7510e" forKey:@"access_token"];
    }
    if ([SystemTool isEnableWIFI] || [SystemTool isEnable3G]){
        if (lastVersion){
            NSString *articleURL = [[NSString alloc] initWithFormat:@"http://10.10.69.53//article/list.xml?access_token=%@&limit=5", _access_token];
            NSData *articleXML = [SystemTool getDataWithSynchronous:articleURL];
            NSArray *array = [SystemTool parseXML:articleXML elementName:@"//article" attributeName:@"key"];
            [articleURL release];
            for(NSMutableDictionary *element in array){
                FMResultSet *rs = [dp getRowEntity:@"Article" primaryKey:[element objectForKey:@"key"]];
                BOOL needDownload = NO;
                if ([rs next]) {
                    needDownload = [SystemTool processUpdateArticleData:element dataBase:dp];
                }else{
                    needDownload = [SystemTool processNewArticleData:element dataBase:dp userId:[[NSUserDefaults standardUserDefaults] integerForKey:@"userid"]];
                }
                if (needDownload){
                    [self startDownload:[element objectForKey:@"download_url"] 
                                    key:[element objectForKey:@"key"] downloadType:@"html"];
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
        }else{
            [dp createUserTable];
            [dp createArticleTable];
            [dp createImageTable];
            [dp createCategoryTable];
            [dp createHistoryTable];
            [dp createOperationTable];
            lastVersion += 1;
            NSDateFormatter *dateFormatter = [[ NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
            NSString * timeString = [dateFormatter stringFromDate:[NSDate date]];
            [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO User (username, login_time) VALUES ('%@', '%@')", username, timeString]];
            [dateFormatter release];
            [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"imageSwitch"];
            [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"syncSwitch"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            int userid = [dp getUserId:username];
            if (userid) {
                [SystemTool createFileDirectory:[NSString stringWithFormat:@"user_%d",userid]];
                [[NSUserDefaults standardUserDefaults] setInteger:userid forKey:@"userid"];
                NSData *data = [SystemTool getDataWithSynchronous:[NSString stringWithFormat:@"http://10.10.69.53/article/list.xml?access_token=%@", _access_token]];
                NSMutableArray *articleXML  = [SystemTool parseXML:data elementName:@"//article" attributeName:@"key"];
                for(NSMutableDictionary *element in articleXML){
                    BOOL needDownload = [SystemTool processNewArticleData:element dataBase:dp userId:[[NSUserDefaults standardUserDefaults] integerForKey:@"userid"]];
                    if (needDownload){
                        [self startDownload:[element objectForKey:@"download_url"] 
                                    key:[element objectForKey:@"key"] downloadType:@"html"];
                        NSString *img_urls = [NSString stringWithString:[element objectForKey:@"image_urls"]];
                        NSMutableArray *urls = [[NSMutableArray alloc] initWithArray:[img_urls componentsSeparatedByString:@"|"]];
                        for(NSString *url in urls){
                            if([url length] >= 1){
                                [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', 0)", [element objectForKey:@"key"], url]];
                                [self startDownload:url key:[[url componentsSeparatedByString:@"/"] lastObject] downloadType:@"image"];
                            }
                        }
                        [urls release];
                    }
                }
            }
        }
    }else{
        NSLog(@"No network");
    }
    NSLog(@"Parse xml success");
    //3g:2, edge:1, wifi:6
    //[_queue setMaxConcurrentOperationCount:6];
    if (lastVersion > [[NSUserDefaults standardUserDefaults] integerForKey:@"lastVersion"]){
        [[NSUserDefaults standardUserDefaults] setInteger:lastVersion forKey:@"lastVersion"];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self
                                          selector:@selector(reachabilityChanged:)
                                          name:@"kReachabilityChangedNotification"
                                          object: nil];
    //[[NSNotificationCenter defaultCenter] postNotificationName:@"kReachabilityChangedNotification" object:nil];
    [dp release];
    self.window.rootViewController = self.navigationController;
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

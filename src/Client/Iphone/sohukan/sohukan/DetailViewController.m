//
//  DetailViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "DetailViewController.h"
#import "DatabaseProcess.h"
#import "sohukanAppDelegate.h"
#import "NSURLProtocolCustom.h"

@implementation DetailViewController
@synthesize webView;
@synthesize switchButton;
@synthesize key;
@synthesize url;
@synthesize isRead;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    self.webView = nil;
    [super didReceiveMemoryWarning];
    
}



-(IBAction)switchReadMode:(id)sender
{   self.switchButton = (UISwitch *)sender;
    if (!self.switchButton.on) {
        [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
        [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.url]]];
    }else{
        [NSURLProtocol registerClass:[NSURLProtocolCustom class]];
        NSString *path = [SystemTool getUserPathForFile:self.key];        
        [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:path]]];
        NSString *html = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
        /*
        NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"sohukan.db"];  
        FMDatabase *db= [FMDatabase databaseWithPath:dbPath];
        [db open];
        FMResultSet *rs = [db executeQuery:[NSString stringWithFormat:@"SELECT * FROM Image WHERE key='%@'",key]];
        NSMutableArray *images = [[NSMutableArray alloc] init];
        while ([rs next]){
            [images addObject:[rs stringForColumn:@"url"]];
        }
        NSString *imgPath = nil;
        for (int i=0; i<[images count]; i++){
            if ([[images objectAtIndex:i] length] > 0){
                imgPath = [[[images objectAtIndex:i] componentsSeparatedByString:@"/"] lastObject];
                NSString *path = [documentsDirectory stringByAppendingPathComponent:imgPath];
                html = [html stringByReplacingOccurrencesOfString:[images objectAtIndex:i] withString:[NSString stringWithFormat:@"file://%@",path]];
            }
        }*/
        [self.webView loadHTMLString:html baseURL:nil];    
    }
}

-(IBAction)switchBackgroundMode:(id)sender
{   
    NSString *js;
    UIImage *readModeImg;
    if (dayMode) {
        js = @"document.body.style.backgroundColor='#000';document.body.style.color='#666';";
        readModeImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"night" ofType:@"png"]];
        [readModeButton setImage:readModeImg];
    }else{
        js = @"document.body.style.backgroundColor='#f3f6f5'";
        readModeImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"day" ofType:@"png"]];
        [readModeButton setImage:readModeImg];
    }
    dayMode = !dayMode;

    [self.webView stringByEvaluatingJavaScriptFromString:js];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == alertView.cancelButtonIndex) {
        return;
    }else{
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        [SystemTool deleteArticleAndImage:dp articleKey:self.key];
        [self.navigationController popViewControllerAnimated:YES];
        [dp release];
    }
}

-(IBAction)markRead:(id)sender
{
    if (self.isRead) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"确定要删除吗" 
                                                        message:@""
                                                       delegate:self 
                                              cancelButtonTitle:@"取消" 
                                              otherButtonTitles:@"确定",nil];
        [alert show];
        [alert release];
        
    }else{
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        [dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET is_read=1 WHERE key='%@'",self.key]];
        [self.navigationController popViewControllerAnimated:YES];
        [dp release];
    //NSArray *allControllers = self.navigationController.viewControllers;
    //UITableViewController *parent = [allControllers lastObject];
    //[parent.tableView reloadData];
    }
}

- (void)observeValueForKeyPath:(NSString*)keyPath ofObject:(id)object
                        change:(NSDictionary*)change context:(void*)context
{
    [object removeObserver:self forKeyPath:keyPath];
    [SystemTool writeApplicationData:((DownloadOperation*)object).data toFile:((DownloadOperation*)object).key];
}

-(void)startDownload:(NSString *)urlString key:(NSString *)keyString downloadType:(NSString *)type
{
    NSURLRequest*  request = [NSURLRequest requestWithURL:[NSURL URLWithString:urlString]
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                                          timeoutInterval: 30.0];
    DownloadOperation*  operation = [[DownloadOperation alloc] initWithRequest:request];
    operation.key = keyString;
    operation.type = type;
    [operation autorelease];
    [operation addObserver:self forKeyPath:@"isFinished"
                   options:NSKeyValueObservingOptionNew context:nil];
    [_queue addOperation:operation];
    
}

-(IBAction) refreshAction:(id)sender
{   if ([SystemTool isEnableWIFI] || [SystemTool isEnable3G]){
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
    NSString *_access_token = [[NSUserDefaults standardUserDefaults] stringForKey:@"access_token"];
    NSString *urlString = [NSString stringWithFormat:@"http://10.10.69.53/article/list.xml?access_token=%@&limit=5", _access_token];
    NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:urlString]
                                             cachePolicy:NSURLRequestUseProtocolCachePolicy
                                         timeoutInterval: 30.0];
    NSURLResponse *response;
    NSError *err;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
    NSMutableArray *array = [SystemTool parseXML:responseData elementName:@"//article" attributeName:@"key"];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    _queue = [[NSOperationQueue alloc] init];
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
            for(NSString *imgURL in urls){
                if([imgURL length] >= 1){
                    [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', 0)", [element objectForKey:@"key"], link]];
                    [self startDownload:url key:[[imgURL componentsSeparatedByString:@"/"] lastObject] downloadType:@"image"];
                }
            }
            [urls release];
        }
        isUpdate = YES;
    }
    [dp closeDB];
    [dp release];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    }
    NSLog(@"Refresh is start");
}


- (void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setToolbarHidden:YES];
    UIImage *leftImg;
    if (self.isRead) {
        leftImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"detail_del" ofType:@"png"]];
        [leftButton setImage:leftImg];
    }else{
        leftImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"detail_marking" ofType:@"png"]];
        [leftButton setImage:leftImg];
    }
    UIImage *readModeImg;
    if (dayMode) {
        readModeImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"day" ofType:@"png"]];
        [readModeButton setImage:readModeImg];
    }else{
        readModeImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"night" ofType:@"png"]];
        [readModeButton setImage:readModeImg];
    }
    self.webView.scalesPageToFit = NO;
    self.webView.delegate = self;
    if (self.switchButton.on){
        [NSURLProtocol registerClass:[NSURLProtocolCustom class]];
        NSString *path = [SystemTool getUserPathForFile:self.key];
        [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:path]]];
        NSString *html = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
        [self.webView loadHTMLString:html baseURL:nil];
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        NSDateFormatter *dateFormatter = [[ NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        NSString * timeString = [dateFormatter stringFromDate:[NSDate date]];
        NSString *sql;
        FMResultSet *rs = [dp getRowEntity:@"History" primaryKey:self.key];
        if ([rs next]) {
            sql = [NSString stringWithFormat:@"UPDATE History SET read_time='%@' WHERE key='%@'", timeString, self.key];
            [dp executeUpdate:sql];
        }else{
            NSMutableArray *array = [[NSMutableArray alloc] init];
            NSMutableArray *historys = [dp getHistoryList:array];
            if ([array count] < 20) {
                sql = [NSString stringWithFormat: @"INSERT INTO History (key, read_time) VALUES ('%@', '%@')", self.key, timeString];
            }else{
                sql = [NSString stringWithFormat: @"UPDATE History SET key='%@',read_time='%@' WHERE key='%@'", self.key, timeString, [historys lastObject]];
                }
            [dp executeUpdate:sql];
            [array release];
        }
        [dateFormatter release];
        [rs close];
        [dp release];
    }else{
        [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
        [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.url]]];
        
    }
    [super viewWillAppear:animated];

}

-(void)viewDidAppear:(BOOL)animated
{   
    [super viewDidAppear:animated];
}

-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

-(void)viewDidLoad
{  
    dayMode = YES;
    [self.view addSubview:self.webView];
    [self.view removeFromSuperview];
    [(UIScrollView *)[[self.webView subviews] objectAtIndex:0] setBounces:NO];
    self.switchButton.on = YES;
    [self.switchButton addTarget:self action:@selector(switchReadMode:) forControlEvents:UIControlEventValueChanged];
    activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [super viewDidLoad];
    
}

    
- (void)viewDidUnload
{ 
    self.key=nil;
    self.webView = nil;
    self.switchButton =nil;
    [activityIndicatorView release];
    leftButton = nil;
    readModeButton = nil;
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    if (navigationType == UIWebViewNavigationTypeLinkClicked) {
        UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:request.URL.absoluteString
                                                           delegate:self 
                                                  cancelButtonTitle:@"取消"
                                             destructiveButtonTitle:nil
                                                  otherButtonTitles:@"收藏", @"在浏览器中打开", nil]; 
        [actionSheet showInView:self.view];
        [actionSheet release];
        return NO;
    }
    return YES;
}


- (void)actionSheet:(UIActionSheet*)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(buttonIndex == actionSheet.cancelButtonIndex) {
        return;
    }
    switch (buttonIndex) {
        case 0: {
            [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
            NSString *myRequestString = [NSString stringWithFormat:@"access_token=%@&url=%@", [[NSUserDefaults standardUserDefaults] stringForKey:@"access_token"], actionSheet.title];
            NSData *myRequestData = [NSData dataWithBytes: [myRequestString UTF8String] length: [myRequestString length]];
            NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://10.10.69.53/article/add.xml/"]];
            [request setHTTPMethod: @"POST"];
            [request setHTTPBody: myRequestData];
            [NSURLConnection sendSynchronousRequest:request returningResponse: nil error: nil];
            [request release];
            break;
        }
        case 1: {
            NSURL *link = [NSURL URLWithString:actionSheet.title];
            [[UIApplication sharedApplication] openURL:link];            
            break;
        }
    }
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{   
    self.webView.backgroundColor = [UIColor blackColor];
    [activityIndicatorView startAnimating];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{   //NSString *js = @"var images=document.images;for(var index in images){images[index].style.display='none';};";
    NSString *js = @"var images=document.images;for(var index in images){images[index].style.maxWidth='100%';};";
    [self.webView stringByEvaluatingJavaScriptFromString:js];
    if (dayMode) {
        [self.webView stringByEvaluatingJavaScriptFromString:@"document.body.style.backgroundColor='#f3f6f5'"];
    }else{
        [self.webView stringByEvaluatingJavaScriptFromString:@"document.body.style.backgroundColor='#000';document.body.style.color='#666';"];
    }
    [activityIndicatorView stopAnimating];
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
}

@end

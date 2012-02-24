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

- (NSData *)applicationDataFromFile:(NSString *)fileName {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *appFile = [documentsDirectory stringByAppendingPathComponent:fileName];
    NSData *myData = [[[NSData alloc] initWithContentsOfFile:appFile] autorelease];
    return myData;
}

-(IBAction)switchReadMode:(id)sender
{   self.switchButton = (UISwitch *)sender;
    if (!self.switchButton.on) {
        [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
        [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.url]]];
    }else{
        [NSURLProtocol registerClass:[NSURLProtocolCustom class]];
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSString *path = [documentsDirectory stringByAppendingPathComponent:self.key];
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
        //[images release];
    
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
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSFileManager *fileManager =[NSFileManager defaultManager];
        [dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key='%@'",self.key]];
        NSString *appFile = [documentsDirectory stringByAppendingPathComponent:self.key];
        [fileManager removeItemAtPath:appFile error:nil];
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
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);  
        NSString *documentDirectory = [paths objectAtIndex:0];  
        NSString *dbPath = [documentDirectory stringByAppendingPathComponent:@"sohukan.db"];  
        FMDatabase *db= [FMDatabase databaseWithPath:dbPath];
        [db open];
        [db executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET is_read=1 WHERE key='%@'",self.key]];
        [self.navigationController popViewControllerAnimated:YES];
    //NSArray *allControllers = self.navigationController.viewControllers;
    //UITableViewController *parent = [allControllers lastObject];
    //[parent.tableView reloadData];
    }
}

-(IBAction)refresh:(id)sender
{
    //NSString *urlString = [NSString stringWithFormat:@"http://10.10.69.53/article/show/%@.xml?access_token=%@"];
    //NSURLRequest *request = [NSURLRequest requestWithURL:
    //                        [NSURL URLWithString:urlString]
    //                                    cachePolicy:NSURLRequestUseProtocolCachePolicy
    //                                    timeoutInterval: 30.0];
    //NSURLResponse *response;
    //NSError *err;
    //NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
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
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSString *path = [documentsDirectory stringByAppendingPathComponent:self.key];
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
        }
        [images release];*/
        [self.webView loadHTMLString:html baseURL:nil];
        
        NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"sohukan.db"]; 
        FMDatabase *db= [FMDatabase databaseWithPath:dbPath];
        [db open];
        FMResultSet *rs = [db executeQuery:[NSString stringWithFormat:@"SELECT * FROM History WHERE key='%@'",self.key]];
        NSDateFormatter *dateFormatter = [[ NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        NSString * timeString = [dateFormatter stringFromDate:[NSDate date]];
        NSString *sql;
        if ([rs next]) {
            sql = [NSString stringWithFormat:@"UPDATE History SET read_time='%@' WHERE key='%@'", timeString, self.key];
            [db executeUpdate:sql];
        }else{
            sql = [NSString stringWithFormat: @"INSERT INTO History (key, read_time) VALUES ('%@', '%@')", self.key, timeString];
            [db executeUpdate:sql];
        }
        [dateFormatter release];
        [rs close];
        [db close];
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
            NSString *myRequestString = [NSString stringWithFormat:@"access_token=%@&url=%@", @"649cfef6a94ee38f0c82a26dc8ad341292c7510e", actionSheet.title];
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

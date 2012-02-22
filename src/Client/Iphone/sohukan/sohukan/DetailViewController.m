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
    if (dayMode) {
        js = @"document.body.style.backgroundColor='#000';document.body.style.color='#FFF';";
        dayMode = NO;
    }else{
        js = @"document.body.style.backgroundColor='#FFF';document.body.style.color='#000';";
        dayMode = YES;
    }
    [self.webView stringByEvaluatingJavaScriptFromString:js];
}

-(IBAction)markRead:(id)sender
{
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
    [self.view addSubview:self.webView];
    [self.view removeFromSuperview];
    [(UIScrollView *)[[self.webView subviews] objectAtIndex:0] setBounces:NO];
    dayMode = YES;//白天
    self.switchButton.on = YES;
    [self.switchButton addTarget:self action:@selector(switchReadMode:) forControlEvents:UIControlEventValueChanged];
    activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [super viewDidLoad];
    
}

    
- (void)viewDidUnload
{ 
    //[self.key release], self.key=nil;
    //[self.webView release], self.webView = nil;
    [activityIndicatorView release];
    [self.switchButton release];
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
        [actionSheet showFromToolbar:self.navigationController.toolbar];
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
    //[self.webView stringByEvaluatingJavaScriptFromString:js];
    if (dayMode) {
        [self.webView stringByEvaluatingJavaScriptFromString:@"document.body.style.backgroundColor='#FFF';document.body.style.color='#000';"];
    }else{
        [self.webView stringByEvaluatingJavaScriptFromString:@"document.body.style.backgroundColor='#000';document.body.style.color='#FFF';"];
    }
    [activityIndicatorView stopAnimating];
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
}

@end

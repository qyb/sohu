//
//  DetailViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "DetailViewController.h"

@implementation DetailViewController
@synthesize webView;
@synthesize key;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)dealloc
{   
    [self.webView release];
    [super dealloc];
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

- (void)viewWillAppear:(BOOL)animated
{   
    self.webView.scalesPageToFit = NO;
    //self.webView.delegate = self;
    [self.webView loadData:[self applicationDataFromFile:self.key] MIMEType:@"text/html" textEncodingName:@"utf-8" baseURL:nil];
    //activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [super viewWillAppear:animated];
}

-(void)viewDidAppear:(BOOL)animated
{   
    NSLog(@"View did appear ");
    [super viewDidAppear:animated];
}
-(void)viewDidLoad
{   NSLog(@"View did load");
    [self.view addSubview:self.webView];
    [self.view removeFromSuperview];
    [super viewDidLoad];
}


- (void)viewDidUnload
{   [self.key release];
    [self.webView release];
    [activityIndicatorView release];
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/*
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

- (NSData *)applicationDataFromFile:(NSString *)fileName {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *appFile = [documentsDirectory stringByAppendingPathComponent:fileName];
    NSData *myData = [[[NSData alloc] initWithContentsOfFile:appFile] autorelease];
    return myData;
}
 -(void)doGET:(NSString *)url_address
 {   
 NSURLRequest *request = [NSURLRequest requestWithURL:
 [NSURL URLWithString:url_address]
 cachePolicy:NSURLRequestUseProtocolCachePolicy
 timeoutInterval: 30.0];
 
 NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
 if (connection) {
 self.responseData = [NSMutableData data];
 }else {
 NSLog (@"The connection failed");
 }
 }

 
- (void)connection:(NSURLConnection *)connection
didReceiveResponse:(NSURLResponse *)response {
    [self.responseData setLength:0];//清空之前的缓存
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [self.responseData appendData:data];
    
}

- (NSCachedURLResponse *)connection:(NSURLConnection *)connection
                  willCacheResponse:(NSCachedURLResponse *)cachedResponse
{
    return cachedResponse;
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    
    //NSLog(@"%@", responseString);
    NSLog(@"Connection did finish");
    //[self writeApplicationData:self.responseData toFile:@"test.html"];
    NSLog(@"Write to file success");
    //[self.webView loadData:self.responseData MIMEType:@"" textEncodingName:@"" baseURL:nil];
    NSString *responseString = [[NSString alloc]
                                initWithData:self.responseData
                                encoding:NSUTF8StringEncoding];
    [self.webView loadHTMLString:responseString baseURL:nil];
    
    [responseString release];
    [connection release];
}

- (void)connection:(NSURLConnection *)connection
  didFailWithError:(NSError *)error
{
    NSLog (@"connection:didFailWithError:");
    NSLog (@"%@",[error localizedDescription]);
    
    [connection release];
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{
    [activityIndicatorView startAnimating];
}
- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    [activityIndicatorView stopAnimating];
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    
}
*/

@end

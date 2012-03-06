//
//  SettingViewController.m
//  sohukan
//
//  Created by  on 12-2-29.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "SettingViewController.h"
#import "sohukanAppDelegate.h"

@implementation SettingViewController
@synthesize username;
@synthesize imageSwitch;
@synthesize syncSwitch;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)viewDidLoad
{   
    self.username.text = [[NSUserDefaults standardUserDefaults] stringForKey:@"username"];
    self.imageSwitch.on = [[NSUserDefaults standardUserDefaults] boolForKey:@"imageSwitch"];
    self.syncSwitch.on = [[NSUserDefaults standardUserDefaults] boolForKey:@"syncSwitch"];
    [self.navigationController setToolbarHidden:YES];
    [self.navigationController setNavigationBarHidden:NO];
    [super viewDidLoad];
}

- (void)viewDidUnload
{
    [delCacheButton release];
    [logoutButton release];
    [checkButton release];
    [helpButton release];
    [feedbackButton release];
    [firstRunView release];
    self.syncSwitch = nil;
    self.imageSwitch = nil;
    self.username = nil;
    [super viewDidUnload];
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void)showFirstRunView:(BOOL)animated {
	[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleBlackOpaque animated:YES];
    if (firstRunView == nil) {
        firstRunView = [[FirstRunViewController alloc] initWithNibName:@"FirstRunViewController" bundle:nil];
    }
    sohukanAppDelegate *myDelegate = (sohukanAppDelegate *)[[UIApplication sharedApplication] delegate];
    [myDelegate.window addSubview:firstRunView.view];
    [self.view removeFromSuperview];
	[UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    //firstRunView.view.frame = [UIScreen mainScreen].applicationFrame;
	[self.view addSubview:firstRunView.view];
}

- (IBAction)logoutAction:(id)sender;
{
    [self showFirstRunView:YES];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"username"];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"access_token"];
    [[NSUserDefaults standardUserDefaults] synchronize];

}
- (IBAction)deleteCache:(id)sender;
{
    
}
- (IBAction)switchSync:(id)sender;
{
    self.syncSwitch = (UISwitch *)sender;
    if (self.syncSwitch.on) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"syncSwitch"];
    }else{
        [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"syncSwitch"];
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
}
- (IBAction)switchImage:(id)sender;
{   
    self.imageSwitch = (UISwitch *)sender;
    if (self.imageSwitch.on) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"imageSwitch"];
    }else{
        [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"imageSwitch"];
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (IBAction)checkVersion:(id)sender;
{

}
- (IBAction)helpAction:(id)sender;
{

}
- (IBAction)feedbackAction:(id)sender;
{

}
@end

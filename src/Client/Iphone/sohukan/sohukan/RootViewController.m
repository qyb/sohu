//
//  RootViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "RootViewController.h"
#import "NotReadViewController.h"
#import "ReadedViewController.h"
#import "RecentViewController.h"
#import "CategoryViewController.h"
#import "FavouriteViewController.h"
#import "SettingViewController.h"

@implementation RootViewController
@synthesize controllers;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (void)viewDidLoad
{   
    self.title = @"首页";
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setToolbarHidden:YES];
    [self.navigationController setNavigationBarHidden:YES];
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload
{   
    self.controllers = nil;
    [super viewDidUnload];
}

- (void)dealloc
{   
    [controllers release];
    [super dealloc];
}

-(IBAction)toNotReadList:(id)sender
{
    NotReadViewController  *notReadController = [[NotReadViewController alloc] initWithStyle:UITableViewStylePlain];
    notReadController.title = @"未读";
    [self.navigationController pushViewController:notReadController animated:YES];
    [notReadController release];

}

-(IBAction)toReadedList:(id)sender
{
    ReadedViewController *readedController = [[ReadedViewController alloc] initWithStyle:UITableViewStylePlain];
    readedController.title = @"已读";
    [self.navigationController pushViewController:readedController animated:YES];
    [readedController release];
    
}

-(IBAction)recentReadList:(id)sender
{
    RecentViewController *recentController = [[RecentViewController alloc] initWithStyle:UITableViewStylePlain];
    recentController.title = @"最近读过";
    [self.navigationController pushViewController:recentController animated:YES];
    [recentController release];
}

-(IBAction)toCategoryList:(id)sender
{
    CategoryViewController *categoryContorller = [[CategoryViewController alloc] initWithStyle:UITableViewStylePlain];
    [self.navigationController pushViewController:categoryContorller animated:YES];
    categoryContorller.title = @"分类";
    [categoryContorller release];
}

-(IBAction)toSetting:(id)sender
{
    SettingViewController *settingController = [[SettingViewController alloc]
                                                initWithStyle:UITableViewStyleGrouped];
    [self.navigationController pushViewController:settingController animated:YES];
    settingController.title = @"设置";
    [settingController release];
}

-(IBAction)toFavourite:(id)sender
{
    FavouriteViewController *favouriteController = [[FavouriteViewController alloc]
                                                initWithNibName:@"FavouriteViewController" bundle:nil];
    [self.navigationController pushViewController:favouriteController animated:YES];
    favouriteController.title = @"如何收藏";
    [favouriteController release];
}
@end

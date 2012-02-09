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

@implementation RootViewController
@synthesize controllers;

- (void)viewDidLoad
{   
    self.title = @"首页";
    NSMutableArray *array = [[NSMutableArray alloc] init];
    NotReadViewController  *notReadController = [[NotReadViewController alloc] initWithStyle:UITableViewStylePlain];
    notReadController.title = @"未读";
    [array addObject:notReadController];
    [notReadController release];
    ReadedViewController *readedController = [[ReadedViewController alloc] initWithStyle:UITableViewStylePlain];
    readedController.title = @"已读";
    [array addObject:readedController];
    [readedController release];
    ListViewController *recentController = [[ListViewController alloc] initWithStyle:UITableViewStylePlain];
    recentController.title = @"最近读过";
    [array addObject:recentController];
    [recentController release];
    self.controllers = array;
    
    [array release];
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated
{
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

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.controllers count];
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }

    NSUInteger row = [indexPath row];
    ListViewController *controller = [controllers objectAtIndex:row];
    cell.textLabel.text = controller.title;
    //cell.image = controller.rowImage;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = [indexPath row];
    ListViewController *nextController = [self.controllers objectAtIndex:row];
    [self.navigationController pushViewController:nextController animated:YES];
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = [indexPath row];
    ListViewController *nextController = [self.controllers objectAtIndex:row];
    [self.navigationController pushViewController:nextController animated:YES];   
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


@end

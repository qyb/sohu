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
    RecentViewController *recentController = [[RecentViewController alloc] initWithStyle:UITableViewStylePlain];
    recentController.title = @"最近读过";
    [array addObject:recentController];
    [recentController release];
    CategoryViewController *categoryContorller = [[CategoryViewController alloc] initWithStyle:UITableViewStylePlain];
    categoryContorller.title = @"分类";
    [array addObject:categoryContorller];
    [categoryContorller release];
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
    return 3;
}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            return 1;
        case 1:
            return [self.controllers count];
        default:
            return 1;
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch ([indexPath section]) {
        case 0:
            return 190;
        case 1:
            return 30;
        default:
            return 100.0;
    }
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";    
    UITableViewCell *cell;
    NSInteger section = [indexPath section];
    switch (section) {
        case 0:
            cell = [tableView dequeueReusableCellWithIdentifier:@"LogoCell"];
            if (cell == nil) {
                cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                               reuseIdentifier:@"LogoCell"] autorelease];
            }
                UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 8, 320, 190)];
                NSString *path = [[NSBundle mainBundle] pathForResource:@"logo_header" ofType:@"png"];
                UIColor *color = [UIColor colorWithPatternImage:[UIImage imageWithContentsOfFile:path]];
                label.backgroundColor = color;
                [cell addSubview:label];
                [label release];
            return cell;
        case 1:
            cell = [tableView dequeueReusableCellWithIdentifier:@"ListCell"];
            if (cell == nil) {
                cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                               reuseIdentifier:@"ListCell"] autorelease];
            }
            ListViewController *controller = [controllers objectAtIndex:[indexPath row]];
            cell.textLabel.text = controller.title;
            //cell.image = controller.rowImage;
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            return cell;
        default:
            cell = [tableView dequeueReusableCellWithIdentifier:@"HelpCell"];
            if (cell == nil) {
                cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                               reuseIdentifier:@"HelpCell"] autorelease];
            }
            UILabel *help = [[UILabel alloc] initWithFrame:CGRectMake(20.0, 500.0, 100.0, 20.0)];
            help.text = @"Help IS HERE";
            [cell addSubview:help];
            [help release];
            return cell;
    }
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
		[cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    }
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    ListViewController *nextController = [self.controllers objectAtIndex:[indexPath row]];
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

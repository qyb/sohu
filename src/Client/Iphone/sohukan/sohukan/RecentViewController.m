//
//  RecentViewController.m
//  sohukan
//
//  Created by  on 12-2-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "RecentViewController.h"
#import "DetailViewController.h"

@implementation RecentViewController
@synthesize articles;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    
    [super viewDidLoad];
}

- (void)viewDidUnload
{
    articles = nil;
    [detailViewController release];
    [super viewDidUnload];
}

- (void)viewWillAppear:(BOOL)animated
{
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.articles = [dp getRecentArticles];
    NSSortDescriptor *timeDescriptor = [[NSSortDescriptor alloc] initWithKey:@"create_time"  
                                                         ascending:YES];  
    NSArray *sortDescriptors = [NSArray arrayWithObject:timeDescriptor];  
    [self.articles sortUsingDescriptors:sortDescriptors];
    [dp closeDB];
    [dp release];
    [timeDescriptor release];
    [self.navigationController setToolbarHidden:NO];
    [self.navigationController setNavigationBarHidden:NO];
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.articles count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"DetailViewCellIdentifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier: 
                             CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle 
                                       reuseIdentifier:CellIdentifier]
                autorelease];
    }
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    cell.textLabel.text = article.title;
    NSURL *link = [[NSURL alloc] initWithString:article.url];
    cell.detailTextLabel.text = link.host;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    [link release];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (detailViewController == nil)
        detailViewController = [[DetailViewController alloc] 
                                initWithNibName:@"DetailViewController" bundle:nil];
    
    Article *article = [articles objectAtIndex:[indexPath row]];
    detailViewController.title = article.title;
    detailViewController.key = article.key;
    [self.navigationController pushViewController:detailViewController
                                         animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0f;
}

@end

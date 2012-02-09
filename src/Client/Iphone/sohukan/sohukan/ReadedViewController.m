//
//  ReadedViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "ReadedViewController.h"
#import "DetailViewController.h"

@implementation ReadedViewController
@synthesize detailViewController;
@synthesize articles;

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
    [articles release];
    [detailViewController release];
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)viewDidLoad
{   
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.articles = [dp getReadedArticles];
    [dp closeDB];
    [dp release];
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    self.articles = nil;
    [detailViewController release];
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (NSInteger)tableView:(UITableView *)tableView 
 numberOfRowsInSection:(NSInteger)section {
    return [self.articles count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView 
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString * DetailViewCellIdentifier = 
    @"DetailViewCellIdentifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier: 
                             DetailViewCellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault 
                                       reuseIdentifier: DetailViewCellIdentifier]
                autorelease];
    }
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    cell.textLabel.text = article.title;
    cell.detailTextLabel.text = article.url;
    cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
    //[article release];
    return cell;
}

- (void)tableView:(UITableView *)tableView 
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    /*
     UIAlertView *alert = [[UIAlertView alloc] initWithTitle:
     @"Hey, do you see the disclosure button?" 
     message:@"If you're trying to drill down, touch that instead"
     delegate:nil 
     cancelButtonTitle:@"Won't happen again" 
     otherButtonTitles:nil];
     [alert show];
     [alert release];
     */
    
}
- (void)tableView:(UITableView *)tableView 
accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
    if (detailViewController == nil)
        detailViewController = [[DetailViewController alloc] 
                                initWithNibName:@"DetailViewController" bundle:nil];
    
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    detailViewController.title = article.title;
    detailViewController.key = article.key;
    //[article release];
    [self.navigationController pushViewController:detailViewController
                                         animated:YES];
}

@end

//
//  ReadedViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "ReadedViewController.h"
#import "DetailViewController.h"
#import "ListItemCell.h"

@implementation ReadedViewController
@synthesize articles;

-(IBAction)toggleEdit:(id)sender
{
    [self.tableView setEditing:!self.tableView.editing animated:YES];
    
    if (self.tableView.editing)
        [self.navigationItem.rightBarButtonItem setTitle:@"确定"];
    else
        [self.navigationItem.rightBarButtonItem setTitle:@"删除"];

}

-(void)flipViewDidFinish:(EditViewController *)controller;
{
    [self dismissModalViewControllerAnimated:YES];
}

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

- (void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setToolbarHidden:NO];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.articles = [dp getReadedArticles];
    [dp closeDB];
    [dp release];
    [self.tableView reloadData];
    [super viewWillAppear:animated];
}

- (void)viewDidLoad
{   
    UIBarButtonItem *delButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"删除"
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(toggleEdit:)];
    self.navigationItem.rightBarButtonItem = delButton;
    [delButton release];
    [self.navigationController setNavigationBarHidden:NO];
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    [self.articles release];
    self.articles = nil;
    [detailViewController release];
    detailViewController = nil;
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
    
    ListItemCell *cell = [tableView dequeueReusableCellWithIdentifier: 
                             DetailViewCellIdentifier];
    if (cell == nil) {
        cell = [[[ListItemCell alloc] initWithStyle:UITableViewCellStyleSubtitle 
                                       reuseIdentifier: DetailViewCellIdentifier]
                                       autorelease];
    }
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    cell.textLabel.text = article.title;
    NSURL *link = [[NSURL alloc] initWithString:article.url];
    cell.detailTextLabel.text = link.host;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.article = article;
    cell.controller = self;
    [link release];
    return cell;
}

- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (detailViewController == nil)
        detailViewController = [[DetailViewController alloc] 
                                initWithNibName:@"DetailViewController" bundle:nil];
    
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    detailViewController.title = article.title;
    detailViewController.key = article.key;
    [self.navigationController pushViewController:detailViewController
                                         animated:YES];
    
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
commitEditingStyle:(UITableViewCellEditingStyle)editingStyle 
forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSUInteger row = [indexPath row];
    Article *article = [self.articles objectAtIndex:row];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"sohukan.db"];  
    FMDatabase *db= [FMDatabase databaseWithPath:dbPath];
    [db open];
    [db executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key='%@'",article.key]];
    //FMResultSet *rs = [db executeQuery:[NSString stringWithFormat:@"SELECT * FROM Image WHERE key='%@'",key]];
    //[dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key='%@'", article.key]];
    //[dp release];
    NSFileManager *fileManager =[NSFileManager defaultManager];
    NSString *appFile = [documentsDirectory stringByAppendingPathComponent:article.key];
    [fileManager removeItemAtPath:appFile error:nil];
    [self.articles removeObjectAtIndex:row];
    [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] 
                     withRowAnimation:UITableViewRowAnimationFade];
    //DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0f;
}

@end

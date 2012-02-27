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

-(void)initButton
{
    UIBarButtonItem *delButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"删除"
                                  style:UIBarButtonItemStyleDone
                                  target:self
                                  action:@selector(delAction:)];
    self.navigationItem.rightBarButtonItem = delButton;
    [delButton release];
    UIImage *orderImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"order" ofType:@"png"]];
    UIImage *syncImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"system_sync" ofType:@"png"]];
    //UIImage *searchImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"searchbar" ofType:@"png"]];
    UIBarButtonItem *orderButton = [[UIBarButtonItem alloc]
                                    initWithImage:orderImg                                   
                                    style:UIBarButtonItemStylePlain
                                    target:self
                                    action:@selector(orderAction:)];
    orderButton.width = 30;
    UISearchBar *searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, 220, 30)];
    searchBar.delegate = self;
    searchBar.showsCancelButton = NO;          
    searchBar.barStyle=UIBarStyleDefault;          
    searchBar.placeholder=@"输入标题"; 
    searchBar.keyboardType=UIKeyboardTypeDefault;
    searchBar.backgroundColor = [UIColor blackColor];
    //UITextField *searchField = [[searchBar subviews] lastObject];
    //[searchField setReturnKeyType:UIReturnKeyGo];
    UIBarButtonItem *searchButton = [[UIBarButtonItem alloc] initWithCustomView:searchBar];
    UIBarButtonItem *refreshButton = [[UIBarButtonItem alloc]
                                      initWithImage:syncImg
                                      style:UIBarButtonItemStylePlain
                                      target:self
                                      action:@selector(refreshAction:)];    
    NSArray *array = [NSArray arrayWithObjects:orderButton, searchButton, refreshButton, nil];
    [orderButton release];
    [refreshButton release];
    [searchButton release];
    [self setToolbarItems:array animated:NO];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == alertView.cancelButtonIndex) {
        return;
    }else{
        if ([delList count]>0) {
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSFileManager *fileManager =[NSFileManager defaultManager];
        for (Article *article in delList) {
            [dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key='%@'",article.key]];
            NSString *appFile = [documentsDirectory stringByAppendingPathComponent:article.key];
            [fileManager removeItemAtPath:appFile error:nil];
        }
        self.articles = [dp getReadedArticles];
        arrayLength = [self.articles count];
        [dp release];
        }else{
            NSLog(@"No element");
        }
    }
    isDel = NO;
    [self initButton];
    [self.tableView reloadData];
}

-(IBAction)saveAction:(id)sender
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"确定要删除吗" 
                                                message:@""
                                                delegate:self 
                                            cancelButtonTitle:@"取消" 
                                           otherButtonTitles:@"确定",nil];
    [alert show];
    [alert release];
    
}
-(IBAction)refreshAction:(id)sender
{
    arrayLength = [self.articles count];
}


-(IBAction)cancelAction:(id)sender
{
    isDel = NO;
    [self initButton];
    [self.tableView reloadData];
}

-(IBAction) orderAction:(id)sender
{
    if (isOrder) {
        [segmentedControl removeFromSuperview];
    }else{
        [self.view addSubview:segmentedControl];
    }
    isOrder = !isOrder;
}


-(IBAction)segmentAction:(id) sender
{   
    NSArray *sortDescriptors;
    NSSortDescriptor *timeDescriptor;
    switch([sender selectedSegmentIndex])
    {   
        case 0:
            timeDescriptor = [[NSSortDescriptor alloc] initWithKey:@"create_time"  
                                                         ascending:NO];  
            sortDescriptors = [NSArray arrayWithObject:timeDescriptor];  
            [self.articles sortUsingDescriptors:sortDescriptors];
            [timeDescriptor release];
            [self.tableView reloadData];
            break;
        case 1:
            timeDescriptor = [[NSSortDescriptor alloc] initWithKey:@"create_time"  
                                                         ascending:YES];  
            sortDescriptors = [NSArray arrayWithObject:timeDescriptor];  
            [self.articles sortUsingDescriptors:sortDescriptors];
            [timeDescriptor release];
            [self.tableView reloadData];
            break;
        default:
            break;
    }
}

-(void)buildSearchResult:(NSString *)matchString
{
    NSMutableArray *res = [[NSMutableArray alloc] init ];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    for (Article *article in [dp getReadedArticles]){
        if ([matchString length] == 0) {
            [res addObject:article];
        }else{
            NSRange range = [article.title rangeOfString:matchString options:NSCaseInsensitiveSearch];
            if (range.location != NSNotFound){
                [res addObject:article];
            }
        }
    }
    self.articles = res;
    [res release];
    [dp release];
    [self.tableView reloadData];

}

-(void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar
{
    if ([self.articles count] != arrayLength) {
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        self.articles = [dp getReadedArticles];
        [self.tableView reloadData];
        [dp release];
    }
}

-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText
{
    if ([searchText length] > 0) {
        [self buildSearchResult:searchText];
    }else{
        [searchBar resignFirstResponder];
    }
}

-(void)searchBarTextDidEndEditing:(UISearchBar *)searchBar
{
}

-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
    [searchBar resignFirstResponder];
}


-(IBAction)delAction:(id)sender
{
    isDel = !isDel;
    self.navigationItem.rightBarButtonItem = nil;
    [self.tableView reloadData];
    UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc]
                                     initWithTitle:@"取消"
                                     style:UIBarButtonItemStyleBordered
                                     target:self
                                     action:@selector(cancelAction:)];
    UIBarButtonItem *saveButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@"保存"
                                   style:UIBarButtonItemStyleDone
                                   target:self
                                   action:@selector(saveAction:)];
    UIBarButtonItem *spaceButton = [[UIBarButtonItem alloc]
                                    initWithTitle:@""
                                    style:UIBarButtonItemStylePlain                      
                                    target:self
                                    action:@selector(cancelAction:)];
    spaceButton.width = 200;
    NSArray *array = [NSArray arrayWithObjects:cancelButton, spaceButton, saveButton, nil];
    [self setToolbarItems:array animated:NO];
    [cancelButton release];
    [saveButton release];
    [spaceButton release];
}

-(IBAction)readyToDel:(id)sender
{
    UIButton *senderButton = (UIButton *)sender;
    UITableViewCell *buttonCell = (UITableViewCell *)[senderButton superview];
    NSUInteger buttonRow = [[self.tableView indexPathForCell:buttonCell] row];
    UIImage *img;
    Article *article = [self.articles objectAtIndex:buttonRow];
    if ([delList containsObject:article]) {
        [delList removeObject:article];
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"list_nodel" ofType:@"png"]];
    }else{
        [delList addObject:article];
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"list_del" ofType:@"png"]];
    }
    [senderButton setImage:img forState:UIControlStateNormal];
}

-(void)flipViewDidFinish:(EditViewController *)controller;
{
    [self dismissModalViewControllerAnimated:YES];
}

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

- (void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setToolbarHidden:NO];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.articles = [dp getReadedArticles];
    arrayLength = [self.articles count];
    [delList removeAllObjects];
    [dp closeDB];
    [dp release];
    [self.tableView reloadData];
    [super viewWillAppear:animated];
}

- (void)viewDidLoad
{   
    delList = [[NSMutableArray alloc] init];
    isOrder = NO;
    isDel = NO;
    [self initButton];
    [self.navigationController setNavigationBarHidden:NO];
    segmentedControl = [[UISegmentedControl alloc] initWithFrame:CGRectMake(0.0f, 333.0f, 320.0f, 40.0f)];
    segmentedControl.tintColor = [UIColor darkGrayColor];
    segmentedControl.backgroundColor = [UIColor darkGrayColor];
    [segmentedControl insertSegmentWithTitle:@"最新" atIndex:0 animated:NO];
    [segmentedControl insertSegmentWithTitle:@"最早" atIndex:1 animated:NO];
    [segmentedControl insertSegmentWithTitle:@"网站" atIndex:2 animated:NO];
    segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
    [segmentedControl addTarget:self action:@selector(segmentAction:)
               forControlEvents:UIControlEventValueChanged];
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    [delList release], delList = nil;
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
    if (isDel) {
        UIImage *delImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"list_nodel" ofType:@"png"]];
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.frame = CGRectMake(0.0, 0.0, delImg.size.width, delImg.size.height);
        [button setImage:delImg forState:UIControlStateNormal];
        [button addTarget:self action:@selector(readyToDel:) forControlEvents:UIControlEventTouchUpInside];
        cell.accessoryView = button;
        cell.accessoryType = UITableViewCellAccessoryNone;
    }else{
        Article *article = [self.articles objectAtIndex:[indexPath row]];
        cell.textLabel.text = article.title;
        NSURL *link = [[NSURL alloc] initWithString:article.url];
        cell.detailTextLabel.text = link.host;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.article = article;
        cell.controller = self;
        cell.accessoryView = nil;
        [link release];
    }
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
    detailViewController.isRead = YES;
    [self.navigationController pushViewController:detailViewController
                                         animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0f;
}
@end
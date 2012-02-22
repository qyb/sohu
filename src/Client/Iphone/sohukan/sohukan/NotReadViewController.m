//
//  NotReadViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "NotReadViewController.h"
#import "DetailViewController.h"
#import "EditViewController.h"
#import "ListItemCell.h"

@implementation NotReadViewController
@synthesize articles;

-(void)saveEdit
{

}

-(IBAction) orderAction:(id)sender
{
    NSLog(@"order");
}

-(IBAction) searchAction:(id)sender
{
    NSLog(@"refresh");
}
-(IBAction) refreshAction:(id)sender
{
    NSLog(@"search");
}

-(IBAction)editAction:(id)sender
{
    isEdit = !isEdit;
    [self.tableView reloadData];
}

-(IBAction)markAction:(id)sender
{
    

}

-(IBAction)readyToMark:(id)sender
{
    NSLog(@"to mark");
    UIButton *senderButton = (UIButton *)sender;
    UITableViewCell *buttonCell = (UITableViewCell *)[senderButton superview];
    NSUInteger buttonRow = [[self.tableView indexPathForCell:buttonCell] row];
    Article *article = [self.articles objectAtIndex:buttonRow];
    [markList addObject:article];
    NSLog(@"#res,%@",markList);
}
- (void)doSearch:(UISearchBar *)searchBar{
	//NSString *email = searchBar.text;
}

-(void)flipViewDidFinish:(EditViewController *)controller;
{
    [self dismissModalViewControllerAnimated:YES];
}

-(void)switchView
{ 
        UIBarButtonItem *addButton = [[UIBarButtonItem alloc]
                                      initWithTitle:@"保存"
                                      style:UIBarButtonItemStyleDone
                                      target:self
                                      action:@selector(saveEdit:)];
        self.navigationItem.rightBarButtonItem = addButton;
        self.navigationItem.title = @"编辑";
        UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc]
                                      initWithTitle:@"取消"
                                      style:UIBarButtonItemStylePlain
                                      target:self
                                      action:@selector(cacelEdit:)];
        self.navigationItem.leftBarButtonItem = cancelButton;
        [addButton release];
        [cancelButton release];
}

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

- (void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setToolbarHidden:NO];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.articles = [dp getNotReadArticles];
    [dp closeDB];
    [dp release];
    [self.tableView reloadData];
    [super viewWillAppear:animated];
}

- (void)viewDidLoad
{   
    //self.tableView.backgroundColor = [UIColor darkGrayColor];
    isEdit = NO;
    UIBarButtonItem *markButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@""
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(editAction:)];
    NSString *markImg = [[NSBundle mainBundle] pathForResource:@"right_mark" ofType:@"png"];
    markButton.image = [UIImage imageWithContentsOfFile:markImg];
    //markButton.image
    self.navigationItem.rightBarButtonItem = markButton;
    UIBarButtonItem *orderButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@"排序"
                                   style:UIBarButtonItemStylePlain
                                   target:self
                                    action:@selector(orderAction:)];
    //UISearchBar *searchButton = [[UISearchBar alloc] initWithFrame:CGRectMake(self.view.bounds.size.width-200, self.view.bounds.size.height-400, self.view.bounds.size.width, 43)];
    //searchButton.delegate = self;
    //searchButton.showsCancelButton = NO;          
    //searchButton.barStyle=UIBarStyleDefault;          
    //searchButton.placeholder=@"输入关键字";           
    //searchButton.keyboardType=UIKeyboardTypeDefault;
    //[self.view addSubview:searchButton];
    //UIToolbarDelegate
    NSString *refreshImg = [[NSBundle mainBundle] pathForResource:@"refresh" ofType:@"png"];
    UIBarButtonItem *searchButton = [[UIBarButtonItem alloc]
                                     initWithTitle:@"搜索"
                                     style:UIBarButtonItemStylePlain
                                     target:self
                                     action:@selector(searchAction:)];
    UIBarButtonItem *refreshButton = [[UIBarButtonItem alloc]
                                      initWithImage:[UIImage imageWithContentsOfFile:refreshImg]
                                      style:UIBarButtonItemStylePlain
                                      target:self
                                      action:@selector(refreshAction:)];
    NSArray *array = [NSArray arrayWithObjects:orderButton, searchButton, refreshButton, nil];
    [orderButton release];
    [markButton release];
    [refreshButton release];
    [searchButton release];
    [self setToolbarItems:array animated:NO];
    [self.navigationController setNavigationBarHidden:NO];
    [self.navigationController setToolbarHidden:NO];
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
    
    ListItemCell *cell = [tableView dequeueReusableCellWithIdentifier: 
                             DetailViewCellIdentifier];
    if (cell == nil) {
        cell = [[[ListItemCell alloc] initWithStyle:UITableViewCellStyleSubtitle 
                                       reuseIdentifier: DetailViewCellIdentifier]
                                       autorelease];
    }
    if (isEdit) {
        NSLog(@"###ISEDIT");
        UIImage *markImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"read_mark" ofType:@"png"]];
        UIImage *markedImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"marked" ofType:@"png"]];
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.frame = CGRectMake(0.0, 0.0, markImg.size.width/2, markImg.size.height/2);
        [button setImage:markImg forState:UIControlStateNormal];
        [button setBackgroundImage:markedImg forState:UIControlEventTouchUpInside];
        [button addTarget:self action:@selector(readyToMark:) forControlEvents:UIControlEventTouchUpInside];
        cell.accessoryView = button;
        cell.accessoryType = UITableViewCellAccessoryNone;
    }else{
        Article *article = [self.articles objectAtIndex:[indexPath row]];
        cell.textLabel.text = article.title;
        cell.article = article;
        cell.controller = self;
        //cell.textLabel.lineBreakMode = UILineBreakModeWordWrap;
        //cell.textLabel.numberOfLines = 2;
        NSURL *link = [[NSURL alloc] initWithString:article.url];
        cell.detailTextLabel.text = link.host;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
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
        detailViewController.url = article.url;
        [self.navigationController pushViewController:detailViewController
                                         animated:YES];
}
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

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0f;
}
@end

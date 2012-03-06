//
//  NotReadViewController.m
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "NotReadViewController.h"
#import "NSURLProtocolCustom.h"
#import "SystemTool.h"
#import "DatabaseProcess.h"

@implementation NotReadViewController
@synthesize articles;
@synthesize changePath;

-(void)initButton
{
    UIImage *markImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"list_mark_logo" ofType:@"png"]];
    UIImage *orderImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"order" ofType:@"png"]];
    UIImage *syncImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"system_sync" ofType:@"png"]];
    UIBarButtonItem *markButton = [[UIBarButtonItem alloc]
                                   initWithImage:markImg
                                   style:UIBarButtonItemStylePlain
                                   target:self
                                   action:@selector(editAction:)];
    self.navigationItem.rightBarButtonItem = markButton;
    UIBarButtonItem *orderButton = [[UIBarButtonItem alloc]
                                    initWithImage:orderImg                                   
                                    style:UIBarButtonItemStylePlain
                                    target:self
                                    action:@selector(orderAction:)];
    orderButton.width = 30;
    theSearchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, 220, 30)];
    //theSearchBar = [[UISearchBar alloc] init];
    theSearchBar.delegate = self;
    theSearchBar.showsCancelButton = NO;          
    theSearchBar.barStyle=UIBarStyleDefault;          
    theSearchBar.placeholder=@"输入标题"; 
    theSearchBar.keyboardType=UIKeyboardTypeDefault;
    theSearchBar.backgroundColor = [UIColor blackColor];
    UIBarButtonItem *searchButton = [[UIBarButtonItem alloc] initWithCustomView:theSearchBar];
    UIBarButtonItem *refreshButton = [[UIBarButtonItem alloc]
                                      initWithImage:syncImg
                                      style:UIBarButtonItemStylePlain
                                      target:self
                                      action:@selector(refreshAction:)]; 
    NSArray *array = [NSArray arrayWithObjects:orderButton, searchButton, refreshButton, nil];
    [orderButton release];
    [markButton release];
    [refreshButton release];
    [searchButton release];
    [self setToolbarItems:array animated:NO];
}

-(IBAction)saveAction:(id)sender
{
    if ([markList count]>0) {
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        for (Article *article in markList) {
            NSString *sql = [NSString stringWithFormat:@"UPDATE Article SET is_read=1 WHERE key='%@'", article.key]; 
            [dp executeUpdate:sql];
        }
        self.articles = [dp getNotReadArticles];
        [dp release];
    }else{
        NSLog(@"No element");
    }
    isEdit = NO;
    [self.tableView reloadData];
    [self initButton];    
}

-(IBAction)cancelAction:(id)sender
{
    [self initButton];
    isEdit = NO;
    [self.tableView reloadData];
}

-(IBAction)orderAction:(id)sender
{
    if (isOrder) {
        [segmentedControl removeFromSuperview];
    }else{
        [[self.tableView superview] addSubview:segmentedControl];
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
//search
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

// dowload file
- (void)observeValueForKeyPath:(NSString*)keyPath ofObject:(id)object
                        change:(NSDictionary*)change context:(void*)context
{
    [object removeObserver:self forKeyPath:keyPath];
    [SystemTool writeApplicationData:((DownloadOperation*)object).data toFile:((DownloadOperation*)object).key];
}

-(void)startDownload:(NSString *)url key:(NSString *)key downloadType:(NSString *)type
{
    NSURLRequest*  request = [NSURLRequest requestWithURL:[NSURL URLWithString:url]
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                                          timeoutInterval: 30.0];
    DownloadOperation*  operation = [[DownloadOperation alloc] initWithRequest:request];
    operation.key = key;
    operation.type = type;
    [operation autorelease];
    [operation addObserver:self forKeyPath:@"isFinished"
                   options:NSKeyValueObservingOptionNew context:nil];
    [_queue addOperation:operation];
    
}

- (void)refreshAction:(id)sender
{   if ([SystemTool isEnableWIFI] || [SystemTool isEnable3G]){
        [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
        [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
        NSString *urlString = [NSString stringWithFormat: @"http://10.10.69.53/article/list.xml?access_token=%@&limit=5",[[NSUserDefaults standardUserDefaults] stringForKey:@"access_token"]];
        NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:urlString]
                                             cachePolicy:NSURLRequestUseProtocolCachePolicy
                                         timeoutInterval: 30.0];
        NSURLResponse *response;
        NSError *err;
        NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
        NSMutableArray *array = [SystemTool parseXML:responseData elementName:@"//article" attributeName:@"key"];
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        _queue = [[NSOperationQueue alloc] init];
        for(NSMutableDictionary *element in array){
            FMResultSet *rs = [dp getRowEntity:@"Article" primaryKey:[element objectForKey:@"key"]];
            BOOL needDownload = NO;
            if ([rs next]) {
                needDownload = [SystemTool processUpdateArticleData:element dataBase:dp];
            }else{
                needDownload = [SystemTool processNewArticleData:element dataBase:dp userId:[[NSUserDefaults standardUserDefaults] integerForKey:@"userid"]];
            }
            if (needDownload){
                [self startDownload:[element objectForKey:@"download_url"] 
                            key:[element objectForKey:@"key"] downloadType:@"html"];
                NSString *img_urls = [NSString stringWithString:[element objectForKey:@"image_urls"]];
                NSMutableArray *urls = [[NSMutableArray alloc] initWithArray:[img_urls componentsSeparatedByString:@"|"]];
                for(NSString *url in urls){
                    NSString *link = [url stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                    if([link length] >= 1){
                        [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', 0)", [element objectForKey:@"key"], link]];
                        [self startDownload:url key:[[url componentsSeparatedByString:@"/"] lastObject] downloadType:@"image"];
                    }
                }
                [urls release];
            }
            isUpdate = YES;
        }
        if (isUpdate) {
            self.articles = [dp getNotReadArticles];
            arrayLength = [self.articles count];
            [self.tableView reloadData];
        }
        [dp closeDB];
        [dp release];
        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    }
}

- (void)editAction:(id)sender
{
    isEdit = !isEdit;
    [self.tableView reloadData];
    self.navigationItem.rightBarButtonItem = nil;
    UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@"取消"
                                   style:UIBarButtonItemStyleBordered
                                   target:self
                                   action:@selector(cancelAction:)];
    UIBarButtonItem *saveButton = [[UIBarButtonItem alloc]
                                     initWithTitle:@"标记已读"
                                     style:UIBarButtonItemStyleDone
                                     target:self
                                     action:@selector(saveAction:)];
    UIBarButtonItem *spaceButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@""
                                   style:UIBarButtonItemStylePlain                      
                                   target:self
                                   action:@selector(cancelAction:)];
    spaceButton.width = 180;
    NSArray *array = [NSArray arrayWithObjects:cancelButton, spaceButton, saveButton, nil];
    [self setToolbarItems:array animated:NO];
    [cancelButton release];
    [saveButton release];
    [spaceButton release];
}

- (void)readyToMark:(id)sender
{
    UIButton *senderButton = (UIButton *)sender;
    UITableViewCell *buttonCell = (UITableViewCell *)[senderButton superview];
    NSUInteger buttonRow = [[self.tableView indexPathForCell:buttonCell] row];
    UIImage *img;
    Article *article = [self.articles objectAtIndex:buttonRow];
    if ([markList containsObject:article]) {
        [markList removeObject:article];
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"list_mark" ofType:@"png"]];
    }else{
        [markList addObject:article];
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"list_marked" ofType:@"png"]];
    }
    [senderButton setImage:img forState:UIControlStateNormal];
}

- (void)flipViewDidFinish:(EditViewController *)controller;
{
    [self dismissModalViewControllerAnimated:YES];
}
//cell swip
- (void)handleLeftSwipe:(UISwipeGestureRecognizer *)recognizer
{
    if (recognizer.state == UIGestureRecognizerStateEnded) {
        if (self.changePath) {
            UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:self.changePath];
            CATransition *animation = [SystemTool swipAnimation:kCATransitionFromRight delegateObject:self];
            [[cell layer] addAnimation:animation forKey:@"kRightAnimationKey"];
            [[[cell.contentView subviews] lastObject] removeFromSuperview];
            self.changePath = nil;
        }
    }
}

- (void)handleRightSwipe:(UISwipeGestureRecognizer *)recognizer
{   
    if (recognizer.state == UIGestureRecognizerStateEnded) {
        NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:[recognizer locationInView:self.view]];
        if (self.changePath == nil) {
            self.changePath = indexPath;
            UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
            CATransition *animation = [SystemTool swipAnimation:kCATransitionFromLeft delegateObject:self];
            [[cell layer] addAnimation:animation forKey:@"kLeftAnimationKey"];
            [cell.contentView addSubview:toolBar];
        }else{
            if ([self.changePath row] != [indexPath row]) {
                UITableViewCell *oldCell = [self.tableView cellForRowAtIndexPath:self.changePath];
                CATransition *animationLeft = [SystemTool swipAnimation:kCATransitionFromRight delegateObject:self];
                [[oldCell layer] addAnimation:animationLeft forKey:@"kRightAnimationKey"];
                [[[oldCell.contentView subviews] lastObject] removeFromSuperview];
                self.changePath = indexPath;
                UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
                CATransition *animationRight = [SystemTool swipAnimation:kCATransitionFromLeft delegateObject:self];
                [[cell layer] addAnimation:animationRight forKey:@"kLeftAnimationKey"];
                [cell.contentView addSubview:toolBar];
            }
        }
    }
}

- (void)changeViewAction:(id)sender
{
    editViewController = [[EditViewController alloc]
                          initWithNibName:@"EditViewController" bundle:nil];
    editViewController.delegate = self;
    editViewController.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    Article *article = [self.articles objectAtIndex:[self.changePath row]];
    editViewController.article = article;
    [self presentModalViewController:editViewController animated:YES]; 
}

-(void)cellDelAction:(id)sender
{
    if (self.changePath) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"确定要删除吗" 
                                                        message:@""
                                                       delegate:self 
                                              cancelButtonTitle:@"取消" 
                                              otherButtonTitles:@"确定",nil];
        [alert show];
        [alert release];
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == alertView.cancelButtonIndex) {
        return;
    }else{
        if (self.changePath) {
            DatabaseProcess *dp = [[DatabaseProcess alloc] init];
            Article *article = [self.articles objectAtIndex:[self.changePath row]];
            [self.tableView beginUpdates];
            [SystemTool deleteArticleAndImage:dp articleKey:article.key];
            [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:self.changePath] withRowAnimation:UITableViewRowAnimationAutomatic];
            self.articles = [dp getNotReadArticles];
            arrayLength = [self.articles count];
            self.changePath = nil;
            [dp release];
            [self.tableView endUpdates];
        }
    }
}

- (void)cellMarkAction:(id)sender
{
    if (self.changePath) {
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        Article *article = [self.articles objectAtIndex:[self.changePath row]];
        [self.tableView beginUpdates];
        [dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET is_read=1 WHERE key='%@'",article.key]];
        [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:self.changePath] withRowAnimation:UITableViewRowAnimationAutomatic];
        self.articles = [dp getNotReadArticles];
        arrayLength = [self.articles count];
        self.changePath = nil;
        [dp release];
        [self.tableView endUpdates];
    }
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        
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
    arrayLength = [self.articles count];
    [markList removeAllObjects];
    [dp closeDB];
    [dp release];
    [self.tableView reloadData];
    [super viewWillAppear:animated];
}

- (void)viewDidLoad
{   
    isEdit = NO;
    isOrder = NO;
    isUpdate = NO;
    markList = [[NSMutableArray alloc] init];
    [self.navigationController setNavigationBarHidden:NO];
    [self.navigationController setToolbarHidden:NO];
    [self initButton];
    segmentedControl = [[UISegmentedControl alloc] initWithFrame:CGRectMake(0.0f, 333.0f, 320.0f, 40.0f)];
    segmentedControl.tintColor = [UIColor darkGrayColor];
    segmentedControl.backgroundColor = [UIColor darkGrayColor];
    [segmentedControl insertSegmentWithTitle:@"最新" atIndex:0 animated:NO];
    [segmentedControl insertSegmentWithTitle:@"最早" atIndex:1 animated:NO];
    [segmentedControl insertSegmentWithTitle:@"网站" atIndex:2 animated:NO];
    segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
    [segmentedControl addTarget:self action:@selector(segmentAction:)
               forControlEvents:UIControlEventValueChanged];
    
    toolBar = [[UIToolbar alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 320.0f, 60.0f)];
    toolBar.barStyle = UIBarStyleBlack;
    //UIImage *bgImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"cell_click" ofType:@"png"]];
    //toolBar.backgroundColor = [UIColor colorWithPatternImage:bgImg];
    UIImage *editImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"cell_edit" ofType:@"png"]];
    UIImage *delImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"cell_del" ofType:@"png"]];
    UIImage *markImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"list_mark" ofType:@"png"]];
    UIBarButtonItem *editButton = [[UIBarButtonItem alloc]
                                   initWithImage:editImg
                                   style:UIBarButtonItemStylePlain
                                   target:self
                                   action:@selector(changeViewAction:)]; 
    UIBarButtonItem *delButton = [[UIBarButtonItem alloc]
                                  initWithImage:delImg
                                  style:UIBarButtonItemStylePlain
                                  target:self
                                  action:@selector(cellDelAction:)]; 
    UIBarButtonItem *markButton = [[UIBarButtonItem alloc]
                                  initWithImage:markImg
                                  style:UIBarButtonItemStylePlain
                                  target:self
                                  action:@selector(cellMarkAction:)]; 
    editButton.width = 100;
    delButton.width = 100;
    markButton.width = 100;
    NSArray *array = [NSArray arrayWithObjects:editButton, delButton, markButton, nil];
    [toolBar setItems:array];
    [delButton release];
    [markButton release];
    [editButton release];
    UISwipeGestureRecognizer* leftRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleLeftSwipe:)];
    leftRecognizer.direction = UISwipeGestureRecognizerDirectionLeft;
    [self.view addGestureRecognizer:leftRecognizer];
    [leftRecognizer release];
    UISwipeGestureRecognizer* rightRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleRightSwipe:)];
    rightRecognizer.direction = UISwipeGestureRecognizerDirectionRight;
    [self.view addGestureRecognizer:rightRecognizer];
    [rightRecognizer release];
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    self.articles = nil;
    self.changePath = nil;
    [toolBar release];
    toolBar = nil;
    [markList release]; 
     markList = nil;
    [detailViewController release];
    [editViewController release];
    [segmentedControl release];
    [_queue release];
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
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle 
                                       reuseIdentifier: DetailViewCellIdentifier]
                                       autorelease];
    }
    if (isEdit) {
        UIImage *markImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"list_mark" ofType:@"png"]];
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.frame = CGRectMake(0.0, 0.0, markImg.size.width/0.5, markImg.size.height/0.5);
        [button setImage:markImg forState:UIControlStateNormal];
        [button addTarget:self action:@selector(readyToMark:) forControlEvents:UIControlEventTouchUpInside];
        cell.accessoryView = button;
        cell.accessoryType = UITableViewCellAccessoryNone;
    }else{
        Article *article = [self.articles objectAtIndex:[indexPath row]];
        cell.textLabel.text = article.title;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.backgroundColor = [UIColor clearColor];
        cell.textLabel.shadowColor = [UIColor whiteColor];
        cell.textLabel.shadowOffset = CGSizeMake(0.0, 1.0);
        NSURL *link = [[NSURL alloc] initWithString:article.url];
        cell.detailTextLabel.text = link.host;
        cell.detailTextLabel.backgroundColor = [UIColor clearColor];
        cell.detailTextLabel.shadowColor = [UIColor whiteColor];
        cell.detailTextLabel.shadowOffset = CGSizeMake(0.0, 1.0);
        [link release];      
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.accessoryView = nil;
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
        detailViewController.isRead = NO;
        [self.navigationController pushViewController:detailViewController
                                         animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0f;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"item_bg" ofType:@"png"]]];
}
@end

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
#import "DownloadOperation.h"
#import "Reachability.h"
#import "NSURLProtocolCustom.h"

@implementation NotReadViewController
@synthesize articles;

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


/// Need to be refactor
-(NSMutableArray *)parseXML:(NSData *)xmlData path:(NSString *)pname attributeName:(NSString *)aname
{
    NSMutableArray *res = [[[NSMutableArray alloc] init] autorelease];
    CXMLDocument *doc = [[[CXMLDocument alloc] initWithData:xmlData options:0 error:nil] autorelease];
    NSArray *nodes = NULL;
    nodes = [doc nodesForXPath:pname error:nil];
    for (CXMLElement *node in nodes) {
        NSMutableDictionary *item = [[NSMutableDictionary alloc] init];
        int counter;
        for(counter = 0; counter < [node childCount]; counter++) {
            [item setObject:[[node childAtIndex:counter] stringValue] forKey:[[node childAtIndex:counter] name]];
        }
        [item setObject:[[node attributeForName:aname] stringValue] forKey:aname];
        [res addObject:item];
        [item release];
    }
    return res;
}

- (BOOL)writeApplicationData:(NSData *)data toFile:(NSString *)fileName {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    if (!documentsDirectory) {
        NSLog(@"Documents directory not found!");        
        return NO;        
    }    
    NSString *appFile = [documentsDirectory stringByAppendingPathComponent:fileName];
    return ([data writeToFile:appFile atomically:YES]);
    
}

- (void)observeValueForKeyPath:(NSString*)keyPath ofObject:(id)object
                        change:(NSDictionary*)change context:(void*)context
{
    [object removeObserver:self forKeyPath:keyPath];
    [self writeApplicationData:((DownloadOperation*)object).data toFile:((DownloadOperation*)object).key];
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

-(void)processArticleData:(NSMutableDictionary *)element dpEntity:(DatabaseProcess *)_dp
{   
    FMResultSet *rs = [_dp getRowEntity:@"Article" primaryKey:[element objectForKey:@"key"]];
    if([rs next]){
        if ([[element objectForKey:@"is_delete"] intValue]){
            [_dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key=%@", [element objectForKey:@"key"]]];
            [_dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Image WHERE key=%@", [element objectForKey:@"key"]]];
        }else{ 
            if (![rs boolForColumn:@"is_download"]){
                [self startDownload:[rs stringForColumn:@"download_url"] key:[rs stringForColumn:@"key"] downloadType:@"html"];
            }
            if ([rs stringForColumn:@"category"] != [element objectForKey:@"category"]){
                [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET category='%@' WHERE key=%@", [element objectForKey:@"category"], [element objectForKey:@"key"]]];
            }
            if ([rs stringForColumn:@"title"] != [element objectForKey:@"category"]){
                [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET title='%@' WHERE key=%@", [element objectForKey:@"title"], [element objectForKey:@"key"]]];
            }
            if ([rs boolForColumn:@"is_read"] != [[element objectForKey:@"is_read"] boolValue]){
                [_dp executeUpdate:[NSString stringWithFormat:@"UPDATE Article SET is_read=%@ WHERE key=%@", [element objectForKey:@"is_read"], [element objectForKey:@"key"]]];
            }
        }
    }else{
        isUpdate = YES;
        if (![[element objectForKey:@"is_delete"] intValue]){
            [_dp insertArticleData:element];
            if ([element objectForKey:@"category"]) {
                [_dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", [element objectForKey:@"category"]]];
            }
            if([element objectForKey:@"download_url"]){
                [self startDownload:[element objectForKey:@"download_url"]
                                key:[element objectForKey:@"key"] 
                       downloadType:@"html"];
            }
            NSString *img_urls = [NSString stringWithString:[element objectForKey:@"image_urls"]];
            NSMutableArray *urls = [[NSMutableArray alloc] initWithArray:[img_urls componentsSeparatedByString:@"|"]];
            for(NSString *url in urls){
                NSString *link = [url stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                if([link length] >= 1){
                    [_dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Image (key, url, is_download) VALUES ('%@', '%@', 0)", [element objectForKey:@"key"], link]];
                    [self startDownload:url key:[[url componentsSeparatedByString:@"/"] lastObject] downloadType:@"image"];
                }
            }
            [urls release];
        }
    }
}

- (BOOL) isEnableWIFI {
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] != NotReachable);
}
/// Need to be refactor

-(IBAction) refreshAction:(id)sender
{   if ([self isEnableWIFI]){
    [NSURLProtocol unregisterClass:[NSURLProtocolCustom class]];
    NSString *url = @"http://10.10.69.53/article/list.xml?access_token=649cfef6a94ee38f0c82a26dc8ad341292c7510e&limit=5";
    NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:url]
                                             cachePolicy:NSURLRequestUseProtocolCachePolicy
                                         timeoutInterval: 30.0];
    NSURLResponse *response;
    NSError *err;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
    NSMutableArray *array = [self parseXML:responseData path:@"//article" attributeName:@"key"];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    _queue = [[NSOperationQueue alloc] init];
    for(NSMutableDictionary *entity in array){
        [self processArticleData:entity dpEntity:(DatabaseProcess *)dp];
        }
    if (isUpdate) {
        self.articles = [dp getNotReadArticles];
        arrayLength = [self.articles count];
        [dp closeDB];
        [dp release];
        [self.tableView reloadData];
    }
    }
    NSLog(@"Refresh is start");
}

-(IBAction)editAction:(id)sender
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

-(IBAction)readyToMark:(id)sender
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
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    self.articles = nil;
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
    ListItemCell *cell = [tableView dequeueReusableCellWithIdentifier: 
                             DetailViewCellIdentifier];
    if (cell == nil) {
        cell = [[[ListItemCell alloc] initWithStyle:UITableViewCellStyleSubtitle 
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
        cell.article = article;
        cell.controller = self;
        //cell.textLabel.lineBreakMode = UILineBreakModeWordWrap;
        //cell.textLabel.numberOfLines = 2;
        NSURL *link = [[NSURL alloc] initWithString:article.url];
        cell.detailTextLabel.text = link.host;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
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
        detailViewController.url = article.url;
        detailViewController.isRead = NO;
        [self.navigationController pushViewController:detailViewController
                                         animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.0f;
}
@end

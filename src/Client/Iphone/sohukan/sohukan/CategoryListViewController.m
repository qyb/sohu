//
//  CategoryListViewController.m
//  sohukan
//
//  Created by  on 12-2-19.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "CategoryListViewController.h"
#import "Models.h"
#import "DetailViewController.h"
#import "DatabaseProcess.h"

@implementation CategoryListViewController
@synthesize articles;
@synthesize changePath;


-(void)flipViewDidFinish:(EditViewController *)controller;
{
    [self dismissModalViewControllerAnimated:YES];
}

-(void)handleLeftSwipe:(UISwipeGestureRecognizer *)recognizer
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

-(void)handleRightSwipe:(UISwipeGestureRecognizer *)recognizer
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

-(void)changeViewAction:(id)sender
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
            self.changePath = nil;
            [self.articles removeObject:article];
            [dp release];
            [self.tableView endUpdates];
        }
    }
}


- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}


- (void)viewDidLoad
{
    toolBar = [[UIToolbar alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 320.0f, 60.0f)];
    toolBar.barStyle = UIBarStyleBlack;
    //UIImage *bgImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"cell_click" ofType:@"png"]];
    //toolBar.backgroundColor = [UIColor colorWithPatternImage:bgImg];
    UIImage *editImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"cell_edit" ofType:@"png"]];
    UIImage *delImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"cell_del" ofType:@"png"]];
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
    NSArray *array = [NSArray arrayWithObjects:editButton, delButton, nil];
    editButton.width = 150;
    delButton.width = 150;
    [toolBar setItems:array];
    [delButton release];
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
    [detailViewController release];
    [super viewDidUnload];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setToolbarHidden:NO];
    [self.navigationController setNavigationBarHidden:NO];
    [self.tableView reloadData];
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


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.articles count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
                                       reuseIdentifier:CellIdentifier] autorelease];
    }
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    cell.textLabel.text = article.title;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
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
    Article *article = [self.articles objectAtIndex:[indexPath row]];
    detailViewController.title = article.title;
    detailViewController.key = article.key;
    detailViewController.url = article.url;
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

//
//  CategoryViewController.m
//  sohukan
//
//  Created by  on 12-2-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "CategoryViewController.h"
#import "NotReadViewController.h"
#import "DatabaseProcess.h"

@implementation CategoryViewController
@synthesize categorys;
@synthesize changePath;

-(IBAction)switchView:(id)sender
{
    addCategoryViewController = [[AddCategory alloc]
                                 initWithNibName:@"AddCategory" bundle:nil];
    addCategoryViewController.delegate = self;
    addCategoryViewController.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    [self presentModalViewController:addCategoryViewController animated:YES];
    /*[UIView beginAnimations:nil context:nil]; 
    [UIView setAnimationDuration:1.0f]; 
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut]; 
    [UIView setAnimationRepeatAutoreverses:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.view cache:YES];
    [UIView commitAnimations];*/
}


- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        
    }
    return self;
}

-(void)flipsideDidFinish:(AddCategory *)controller;
{
    [self dismissModalViewControllerAnimated:YES];
    [self.tableView reloadData];
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
    addCategoryViewController = [[AddCategory alloc]
                          initWithNibName:@"AddCategory" bundle:nil];
    addCategoryViewController.delegate = self;
    addCategoryViewController.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    Category *category = [self.categorys objectAtIndex:[self.changePath row]];
    addCategoryViewController.category = category;
    [self presentModalViewController:addCategoryViewController animated:YES]; 
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
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        Category *category = [self.categorys objectAtIndex:[self.changePath row]];
        [self.tableView beginUpdates];
        [dp executeUpdate:[NSString stringWithFormat:@"DELETE FROM Category WHERE name='%@'",category.name]];
        [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:self.changePath] withRowAnimation:UITableViewRowAnimationAutomatic];
        self.categorys = [dp getAllCategory];
        self.changePath = nil;
        [dp release];
        [self.tableView endUpdates];
    }
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    UIBarButtonItem *addButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@"添加"
                                   style:UIBarButtonItemStyleBordered
                                   target:self
                                  action:@selector(switchView:)];
    self.navigationItem.rightBarButtonItem = addButton;
    [addButton release];
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
    self.categorys = nil;
    self.changePath = nil;
    [categoryListViewController release];
    [addCategoryViewController release];
    [super viewDidUnload];
}

- (void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setToolbarHidden:NO];
    [self.navigationController setNavigationBarHidden:NO];
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.categorys = [dp getAllCategory];
    [dp closeDB];
    [dp release];
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


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.categorys count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    Category *category = [self.categorys objectAtIndex:[indexPath row]];
    cell.textLabel.text = category.name;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    UIImage *image = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"category_icon" ofType:@"png"]];
    cell.imageView.image = image;
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{   
    if (categoryListViewController == nil ) {
        categoryListViewController = [[CategoryListViewController alloc] initWithStyle:UITableViewStylePlain];

    }
    Category *category = [self.categorys objectAtIndex:[indexPath row]];
    categoryListViewController.title = category.name;
    categoryListViewController.articles = category.articles;
    [self.navigationController pushViewController:categoryListViewController animated:YES];
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

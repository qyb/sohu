//
//  CategoryViewController.m
//  sohukan
//
//  Created by  on 12-2-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "CategoryViewController.h"
#import "NotReadViewController.h"

@implementation CategoryViewController
@synthesize categorys;

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

-(void)flipsideDidFinish:(AddCategory *)controller
{
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    self.categorys = [dp getAllCategory];
    [dp closeDB];
    [dp release];
    [self.tableView reloadData];
    [self dismissModalViewControllerAnimated:YES];
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
    [super viewDidLoad];
    
}

- (void)viewDidUnload
{       
    //[self.categorys release];
    self.categorys = nil;
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

@end

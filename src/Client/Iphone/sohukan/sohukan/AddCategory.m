//
//  AddCategory.m
//  sohukan
//
//  Created by  on 12-2-17.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "AddCategory.h"
#import "DatabaseProcess.h"

@implementation AddCategory
@synthesize textField;  
@synthesize delegate;
@synthesize category;

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
    [super viewWillAppear:animated];
    textField.text = self.category.name;
    
}

- (void)viewDidLoad
{   
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    [textField release];
    textField = nil;
    self.category = nil;
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(IBAction)cancelAction:(id)sender
{
    [self.delegate flipsideDidFinish:self];
}

-(IBAction)saveAction:(id)sender
{
    if ([textField.text length] >= 1){
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        FMResultSet *rs = [dp getCategoryEntity:self.category.name];
        if ([rs next]) {
            [dp executeUpdate:[NSString stringWithFormat:@"UPDATE Category SET name='%@' WHERE name='%@'", textField.text, self.category.name]];
        }else{
            [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", textField.text]];
        }
        [dp release];
    }
    [self.delegate flipsideDidFinish:self];
    
}

@end

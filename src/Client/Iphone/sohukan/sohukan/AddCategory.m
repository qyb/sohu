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
@synthesize category;  
@synthesize delegate;
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

- (void)viewDidLoad
{   
    [super viewDidLoad];
}

- (void)viewDidUnload
{   
    [category release];
    category = nil;
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(IBAction)cancelAction:(id)sender
{
    [self.delegate flipsideDidFinish:self];
}
-(IBAction)saveAction:(id)sender
{
    if ([category.text length] >= 1){
        DatabaseProcess *dp = [[DatabaseProcess alloc] init];
        [dp executeUpdate:[NSString stringWithFormat:@"INSERT INTO Category (name) VALUES ('%@')", category.text]];
        [dp release];
    }
    [self.delegate flipsideDidFinish:self];
    
}

@end

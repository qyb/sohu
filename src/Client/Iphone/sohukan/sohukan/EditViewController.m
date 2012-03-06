//
//  EditViewController.m
//  sohukan
//
//  Created by  on 12-2-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "EditViewController.h"
#import "DatabaseProcess.h"
#import "SystemTool.h"

@implementation EditViewController
@synthesize article;
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
    _url.text = self.article.url;
    _titleContent.text = self.article.title;
    _category.text = self.article.category;
    isRead = self.article.is_read;
    isDel = NO;
    UIImage *nodelImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_nodel" ofType:@"png"]];
    [delButton setBackgroundImage:nodelImg forState:UIControlStateNormal];
    UIImage *markImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_mark" ofType:@"png"]];
    [markButton setBackgroundImage:markImg forState:UIControlStateNormal];
    [super viewDidLoad];
}

- (void)viewDidUnload
{
    [_url release];
    [_titleContent release];
    [_category release];
    [article release];
    [super viewDidUnload];
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(IBAction)cancelAction:(id)sender
{
    [self.delegate flipViewDidFinish:self];

}

-(IBAction)readyToDel:(id)sender
{
    UIButton *button = (UIButton*)sender;
    UIImage *img;
    if (isDel){
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_del" ofType:@"png"]];
    }else{
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_nodel" ofType:@"png"]];
    }
    [button setBackgroundImage:img forState:UIControlStateNormal];
    isDel = !isDel;
    
}

-(IBAction)readyToMark:(id)sender
{
    UIButton *button = (UIButton *)sender;
    UIImage *img;
    if (isRead) {
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_marked" ofType:@"png"]];
    }else{
        img = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_mark" ofType:@"png"]];
               }
        [button setBackgroundImage:img forState:UIControlStateNormal];
    isRead = !isRead;

}
-(IBAction)saveAction:(id)sender
{   
    
    DatabaseProcess *dp = [[DatabaseProcess alloc] init];
    if (isDel){
        [SystemTool deleteArticleAndImage:dp articleKey:article.key];
    }else{
        FMResultSet *rs = [dp getCategoryEntity:_category.text];
        if (![rs next]){
            NSString *inserSQl = [[NSString alloc] initWithFormat:@"INSERT INTO Category (name) VALUES ('%@')",_category.text];
            [dp executeUpdate:inserSQl];
            [inserSQl release];
        }
        NSString *updateSQL = [[NSString alloc] initWithFormat:@"UPDATE Article SET title='%@',category='%@',is_read=%d WHERE key='%@'", _titleContent.text, _category.text, isRead, self.article.key];
        [dp executeUpdate:updateSQL];
        [updateSQL release];
    }
    [dp release];
    [self.delegate flipViewDidFinish:self];
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

@end

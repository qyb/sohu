//
//  EditViewController.m
//  sohukan
//
//  Created by  on 12-2-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "EditViewController.h"
#import "DatabaseProcess.h"

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

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    _url.text = self.article.url;
    _titleContent.text = self.article.title;
    _category.text = self.article.category;
    isRead = self.article.is_read;
    isDel = NO;
    /*
    UIImage *nodelImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_nodel" ofType:@"png"]];
    UIImage *delImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_nodel" ofType:@"png"]];
    [delButton setBackgroundImage:nodelImg forState:UIControlStateNormal];
    [delButton setBackgroundImage:delImg forState:UIControlEventTouchUpInside];
    UIImage *markImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_mark" ofType:@"png"]];
    UIImage *markedImg = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"edit_mark" ofType:@"png"]];
    [markButton setBackgroundImage:markImg forState:UIControlStateNormal];
    [markButton setBackgroundImage:markedImg forState:UIControlEventTouchUpInside];
*/
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
    isDel = !isDel;
    
}

-(IBAction)readyToMark:(id)sender
{
    isRead = !isRead;

}
-(IBAction)saveAction:(id)sender
{   
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"sohukan.db"];  
    FMDatabase *db= [FMDatabase databaseWithPath:dbPath];
    [db open];
    if (isDel){
        NSFileManager *fileManager =[NSFileManager defaultManager];
        
        NSString *appFile = [documentsDirectory stringByAppendingPathComponent:self.article.key];
        [fileManager removeItemAtPath:appFile error:nil];
        //TO-DO DELETE IMAGE FILE;
        [db executeUpdate:[NSString stringWithFormat:@"DELETE FROM Article WHERE key='%@'", self.article.key]];
    }else{
        NSString *selSQL = [[NSString alloc] initWithFormat:@"SELECT id FROM Category WHERE name='%@'",_category.text];
        if(![db boolForQuery:selSQL]){
            NSString *inserSQl = [[NSString alloc] initWithFormat:@"INSERT INTO Category (name) VALUES ('%@')",_category.text];
            [db executeUpdate:inserSQl];
            [inserSQl release];
        }
        NSString *updateSQL = [[NSString alloc] initWithFormat:@"UPDATE Article SET title='%@',category='%@',is_read=%d WHERE key='%@'", _titleContent.text, _category.text, isRead, self.article.key];
        [db executeUpdate:updateSQL];
        [updateSQL release];
        [selSQL release];
    }
    [self.delegate flipViewDidFinish:self];
}
@end

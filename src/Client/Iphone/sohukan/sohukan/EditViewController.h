//
//  EditViewController.h
//  sohukan
//
//  Created by  on 12-2-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Models.h"
@protocol FlipViewDidDelegate;

@class Article;
@interface EditViewController : UIViewController{
    IBOutlet UILabel *_url;
    IBOutlet UITextField *_titleContent;
    IBOutlet UITextField *_category;
    IBOutlet UIButton *markButton;
    IBOutlet UIButton *delButton;
    id <FlipViewDidDelegate> delegate;
    Article *article;
    int isRead;
    BOOL isDel;
}
@property(nonatomic, assign)Article *article;
@property(nonatomic, assign)id <FlipViewDidDelegate> delegate;
-(IBAction)cancelAction:(id)sender;
-(IBAction)saveAction:(id)sender;
@end

@protocol FlipViewDidDelegate
-(void)flipViewDidFinish:(EditViewController *)controller;
@end
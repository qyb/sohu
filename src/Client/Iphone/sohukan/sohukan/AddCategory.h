//
//  AddCategory.h
//  sohukan
//
//  Created by  on 12-2-17.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Models.h"

@protocol FlipSideDelegate;

@interface AddCategory : UIViewController{
    IBOutlet UITextField *textField;
    id <FlipSideDelegate> delegate;
    Category *category;
}
@property(nonatomic, retain)IBOutlet UITextField *textField;
@property(nonatomic, retain)Category *category;
@property(nonatomic, assign) id <FlipSideDelegate> delegate;
-(IBAction)cancelAction:(id)sender;
-(IBAction)saveAction:(id)sender;
@end

@protocol FlipSideDelegate
-(void)flipsideDidFinish:(AddCategory *)controller;
@end
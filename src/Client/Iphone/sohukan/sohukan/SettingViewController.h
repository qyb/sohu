//
//  SettingViewController.h
//  sohukan
//
//  Created by  on 12-2-29.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FirstRunViewController.h"

@interface SettingViewController : UIViewController{
    IBOutlet UIButton *logoutButton;
    IBOutlet UIButton *delCacheButton;
    IBOutlet UIButton *checkButton;
    IBOutlet UIButton *helpButton;
    IBOutlet UIButton *feedbackButton;
    IBOutlet UISwitch *syncSwitch;
    IBOutlet UISwitch *imageSwitch;
    IBOutlet UILabel *username;
    FirstRunViewController *firstRunView;
}
@property(retain, nonatomic)IBOutlet UISwitch *syncSwitch;
@property(retain, nonatomic)IBOutlet UISwitch *imageSwitch;
@property(retain, nonatomic)IBOutlet UILabel *username;
- (IBAction)logoutAction:(id)sender;
- (IBAction)deleteCache:(id)sender;
- (IBAction)switchSync:(id)sender;
- (IBAction)switchImage:(id)sender;
- (IBAction)checkVersion:(id)sender;
- (IBAction)helpAction:(id)sender;
- (IBAction)feedbackAction:(id)sender;
@end

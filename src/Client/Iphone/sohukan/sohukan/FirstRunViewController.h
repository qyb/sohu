//
//  ListViewController.h
//  sohukan
//
//  Created by  on 12-2-28.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@interface FirstRunViewController : UIViewController <UITextFieldDelegate> {
	IBOutlet UITextField *_username;
	IBOutlet UITextField *_password;
	IBOutlet UITextField *_regusername;
	IBOutlet UITextField *_regpassword;
	IBOutlet UITextField *_regemail;
	IBOutlet UIView *_registrationView;
}
-(IBAction)loginButtonPressed:(id)sender;
-(IBAction)registerButtonPressed:(id)sender;
-(IBAction)createButtonPressed:(id)sender;
-(IBAction)cancelButtonPressed:(id)sender;
-(BOOL)textFieldShouldReturn:(UITextField *)textField;
-(void)dismissKeyboard;
@end

@interface FirstRunViewBackground : UIImageView {
	IBOutlet FirstRunViewController *delegate;
}
@end

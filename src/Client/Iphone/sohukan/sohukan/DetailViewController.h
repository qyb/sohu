//
//  DetailViewController.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DatabaseProcess.h"
#import "SystemTool.h"
#import "DownloadOperation.h"

@interface DetailViewController : UIViewController <UIWebViewDelegate, UIActionSheetDelegate>{
    IBOutlet UIWebView *webView;
    IBOutlet UISwitch *switchButton;
    IBOutlet UIActivityIndicatorView *activityIndicatorView;
    IBOutlet UIBarButtonItem *leftButton;
    IBOutlet UIBarButtonItem *readModeButton;
    NSOperationQueue *_queue;
    NSString *key;
    NSString *url;
    BOOL dayMode;
    BOOL isRead;
    BOOL isUpdate;
}
@property(assign, nonatomic)NSString *key;
@property(assign, nonatomic)NSString *url;
@property(assign, nonatomic)BOOL isRead;
@property(retain, nonatomic)IBOutlet UIWebView *webView;
@property(retain, nonatomic)IBOutlet UISwitch *switchButton;
- (IBAction) switchReadMode:(id)sender;
- (IBAction) switchBackgroundMode:(id)sender;
- (IBAction) markRead:(id)sender;
- (IBAction) refreshAction:(id)sender;
@end

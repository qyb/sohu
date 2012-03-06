//
//  sohukanAppDelegate.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CXMLDocument.h"
#import "DatabaseProcess.h"
#import "FirstRunViewController.h"
#import "GuideViewController.h"

@interface sohukanAppDelegate : NSObject <UIApplicationDelegate> {
    NSOperationQueue *_queue;
    UIView *_mainView;
    FirstRunViewController *firstRunView;
    GuideViewController *guideView;

}
@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UINavigationController *navigationController;
@end

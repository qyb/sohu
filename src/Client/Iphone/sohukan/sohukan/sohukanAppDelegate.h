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

@interface sohukanAppDelegate : NSObject <UIApplicationDelegate> {
    NSOperationQueue *_queue;
    DatabaseProcess *_dp;
}
@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UINavigationController *navigationController;
@end

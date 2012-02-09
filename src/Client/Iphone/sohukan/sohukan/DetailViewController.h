//
//  DetailViewController.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface DetailViewController : UIViewController {
    IBOutlet UIWebView *webView;
    UIActivityIndicatorView *activityIndicatorView;
    NSString *key;
}
@property(retain, nonatomic)NSString *key;
@property(retain, nonatomic)IBOutlet UIWebView *webView;

@end

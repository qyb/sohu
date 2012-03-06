//
//  GuideViewController.h
//  sohukan
//
//  Created by  on 12-2-29.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GuideViewController : UIViewController
{
    UIView *_mainView;
    NSArray *images;
    int imageCount;
}
@property(retain, nonatomic)NSArray *images;
@end

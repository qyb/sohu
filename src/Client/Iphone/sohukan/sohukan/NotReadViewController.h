//
//  NotReadViewController.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ListViewController.h"
#import "EditViewController.h"

@class DetailViewController;
@interface NotReadViewController : ListViewController<FlipViewDidDelegate> {
    NSMutableArray *articles;
    NSMutableArray *markList;
    DetailViewController *detailViewController;
    EditViewController *editViewController;
    BOOL isEdit;

}
@property(retain, nonatomic)NSMutableArray *articles;

@end

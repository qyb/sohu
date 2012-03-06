//
//  ReadedViewController.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EditViewController.h"
#import "ListViewController.h"

@class DetailViewController;
@interface ReadedViewController : ListViewController <FlipViewDidDelegate, UIAlertViewDelegate, UISearchBarDelegate>{
    NSMutableArray *articles;
    NSMutableArray *delList;
    DetailViewController *detailViewController;
    UISegmentedControl *segmentedControl;
    NSOperationQueue *_queue;
    UIToolbar *toolBar;
    NSIndexPath *changePath;
    EditViewController *editViewController;
    BOOL isOrder;
    BOOL isDel;
    BOOL isUpdate;
    int arrayLength;
}
@property(retain, nonatomic)NSMutableArray *articles;
@property(retain, nonatomic)NSIndexPath *changePath;

@end

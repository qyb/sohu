//
//  ReadedViewController.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ListViewController.h"
#import "EditViewController.h"

@class DetailViewController;
@interface ReadedViewController : ListViewController <FlipViewDidDelegate, UIAlertViewDelegate, UISearchBarDelegate>{
    NSMutableArray *articles;
    NSMutableArray *delList;
    DetailViewController *detailViewController;
    UISegmentedControl *segmentedControl;
    BOOL isOrder;
    BOOL isDel;
    int arrayLength;
}
@property(retain, nonatomic)NSMutableArray *articles;
@end

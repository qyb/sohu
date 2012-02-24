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
#import "CXMLDocument.h"

@class DetailViewController;
@interface NotReadViewController : ListViewController<FlipViewDidDelegate, UISearchBarDelegate> {
    NSMutableArray *articles;
    NSMutableArray *markList;
    DetailViewController *detailViewController;
    EditViewController *editViewController;
    UISegmentedControl *segmentedControl;
    UISearchBar *theSearchBar;
    NSOperationQueue *_queue;
    BOOL isEdit;
    BOOL isOrder;
    BOOL isUpdate;
    BOOL inSearchMode;
    int arrayLength;
}
@property(retain, nonatomic)NSMutableArray *articles;

@end

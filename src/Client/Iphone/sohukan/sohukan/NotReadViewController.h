//
//  NotReadViewController.h
//  sohukan
//
//  Created by riven on 12-1-15.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ListViewController.h"
@class DetailViewController;
@interface NotReadViewController : ListViewController{
    NSMutableArray *articles;
    DetailViewController *detailViewController;
}
@property(nonatomic, retain)NSMutableArray *articles;
@property(nonatomic, retain)DetailViewController *detailViewController;
@end

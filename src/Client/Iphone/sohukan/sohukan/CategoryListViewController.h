//
//  CategoryListViewController.h
//  sohukan
//
//  Created by  on 12-2-19.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EditViewController.h"
#import "ListViewController.h"

@class DetailViewController;
@interface CategoryListViewController : ListViewController<FlipViewDidDelegate>{
    NSMutableArray *articles;
    DetailViewController *detailViewController;
    UIToolbar *toolBar;
    NSIndexPath *changePath;
    EditViewController *editViewController;
}
@property(nonatomic, retain)NSMutableArray *articles;
@property(nonatomic, retain)NSIndexPath *changePath;
@end

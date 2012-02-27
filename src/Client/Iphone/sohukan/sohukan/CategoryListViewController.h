//
//  CategoryListViewController.h
//  sohukan
//
//  Created by  on 12-2-19.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ListViewController.h"
#import "EditViewController.h"

@class DetailViewController;
@interface CategoryListViewController : ListViewController<FlipViewDidDelegate> {
    NSMutableArray *articles;
    DetailViewController *detailViewController;
}
@property(nonatomic, assign)NSMutableArray *articles;
@end

//
//  RecentViewController.h
//  sohukan
//
//  Created by  on 12-2-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ListViewController.h"

@class DetailViewController;
@interface RecentViewController : ListViewController{
    NSMutableArray *articles;
    DetailViewController *detailViewController;
}
@end

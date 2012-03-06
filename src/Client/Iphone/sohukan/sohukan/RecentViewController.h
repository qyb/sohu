//
//  RecentViewController.h
//  sohukan
//
//  Created by  on 12-2-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class DetailViewController;
@interface RecentViewController : UITableViewController{
    NSMutableArray *articles;
    DetailViewController *detailViewController;
}
@property(retain, nonatomic)NSMutableArray *articles;
@end

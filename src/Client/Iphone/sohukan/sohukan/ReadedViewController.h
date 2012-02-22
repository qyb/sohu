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
@interface ReadedViewController : ListViewController <FlipViewDidDelegate>{
    NSMutableArray *articles;
    DetailViewController *detailViewController;
}
@property(retain, nonatomic)NSMutableArray *articles;
-(IBAction)toggleEdit:(id)sender;
@end

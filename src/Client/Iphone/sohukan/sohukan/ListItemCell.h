//
//  ListItemCell.h
//  sohukan
//
//  Created by  on 12-2-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EditViewController.h"

@class Article;
@class ListViewController;
@interface ListItemCell : UITableViewCell{
    EditViewController *editViewController;
    Article *article;
    ListViewController *controller;

}
@property(nonatomic, assign)Article *article;
@property(nonatomic, assign)ListViewController *controller;
@end
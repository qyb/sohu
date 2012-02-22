//
//  CategoryViewController.h
//  sohukan
//
//  Created by  on 12-2-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ListViewController.h"
#import "AddCategory.h"
#import "CategoryListViewController.h"

@class CategoryListViewController;
@class AddCategory;
@interface CategoryViewController : ListViewController <FlipSideDelegate>{
    NSMutableArray *categorys;
    CategoryListViewController *categoryListViewController;
    AddCategory *addCategoryViewController;
}
@property(nonatomic, retain)NSMutableArray *categorys;
-(IBAction)switchView:(id)sender;
@end
//
//  Article.h
//  sohukan
//
//  Created by riven on 12-2-2.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface Article : NSObject {
    NSString* key;
    NSString* title;
    NSString* url;
    NSString* download_url;
    NSString* cover;
    NSString* category;
    NSDate*   create_time;
    BOOL      is_star;
    BOOL      is_ead;
    BOOL      is_download;
}
@property(retain, nonatomic)NSString* key;
@property(retain, nonatomic)NSString* title;
@property(retain, nonatomic)NSString* url;
@property(retain, nonatomic)NSString* download_url;
@property(retain, nonatomic)NSString* cover;
@property(retain, nonatomic)NSString* category;
@property(retain, nonatomic)NSDate* create_time;
@property(nonatomic) BOOL is_star;
@property(nonatomic) BOOL is_read;
@property(nonatomic) BOOL is_download; 

@end

@interface  Category : NSObject {
    NSString* name;
    NSMutableArray* articles;
}
@property(retain, nonatomic)NSString* name;
@property(retain, nonatomic)NSMutableArray* articles;

@end



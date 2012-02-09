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
    BOOL      is_star;
    BOOL      is_read;
    BOOL      is_download;
    BOOL      is_delete;
}
@property(retain, nonatomic)NSString* key;
@property(retain, nonatomic)NSString* title;
@property(retain, nonatomic)NSString* url;
@property(retain, nonatomic)NSString* download_url;
@property(retain, nonatomic)NSString* cover;
@property(retain, nonatomic)NSString* category;
@property(nonatomic) BOOL is_star;
@property(nonatomic) BOOL is_read;
@property(nonatomic) BOOL is_download; 
@property(nonatomic) BOOL is_delete; 

@end


@interface Image : NSObject {
    NSString* key;
    NSString* url;
    BOOL      is_download;
}
@property(retain, nonatomic)NSString* key;
@property(retain, nonatomic)NSString* url;
@property(nonatomic)BOOL is_download;

@end

@interface  Category : NSObject {
    NSString* name;
}
@property(retain, nonatomic)NSString* name;

@end

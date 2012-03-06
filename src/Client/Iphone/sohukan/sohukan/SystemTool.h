//
//  SystemTool.h
//  sohukan
//
//  Created by  on 12-2-25.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <QuartzCore/QuartzCore.h>
#import <CommonCrypto/CommonDigest.h>
#import "Reachability.h"
#import "CXMLDocument.h"
#import "DatabaseProcess.h"
#import "DownloadOperation.h"
#import "Models.h"


@interface SystemTool : NSObject{
}
+ (BOOL)isEnableWIFI;
+ (BOOL)isEnable3G;
+ (void)reachabilityChanged:(NSNotification *)note;
+ (BOOL)writeApplicationData:(NSData *)data toFile:(NSString *)fileName;
+ (NSString *)getUserDirectory;
+ (NSString *)getUserPathForFile:(NSString *)fileName;
+ (BOOL)createFileDirectory:(NSString*)name;
+ (NSData *)getDataWithSynchronous:(NSString *)urlString;
+ (NSMutableArray *)parseXML:(NSData *)xmlData elementName:(NSString *)elementName attributeName:(NSString *)attributeName;
+ (UITableViewCell *)setCellStyle:(UITableViewCell *)cell detailLink:(NSString *)urlString;
+ (void)processCagegoryData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp;
+ (BOOL)processUpdateArticleData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp;
+ (BOOL)processNewArticleData:(NSMutableDictionary *)element dataBase:(DatabaseProcess *)dp userId:(int)userid;
+ (CATransition *)swipAnimation:(NSString *)direction delegateObject:(id)delegate;
+ (BOOL)deleteArticleAndImage:(DatabaseProcess *)dp articleKey:(NSString *)key;
+ (NSString *)md5:(NSString *)orignalString;
@end

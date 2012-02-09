//
//  DatabaseProcess.h
//  sohukan
//
//  Created by riven on 12-2-2.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FMDatabase.h"
#import "FMDatabaseAdditions.h"

@interface DatabaseProcess : NSObject {
    FMDatabase *_db;
}
-(FMDatabase *)connectionDB;
-(void)createArticleTable;
-(void)createImageTable;
-(void)createCategoryTable;
-(BOOL)tableExist:(NSString *)name;
-(void)insertArticleData:(NSMutableDictionary *)data;
-(void)insertImageData:(NSMutableDictionary *)data;
-(void)insertCategoryData:(NSMutableDictionary *)data;
-(void)updateBoolData:(NSString *)tableName primaryKey:(NSString *)key columnName:(NSString *)name setValue:(int)value;
-(void)updateStringData:(NSString *)tableName primaryKey:(NSString *)key columnName:(NSString *)name setValue:(NSString *)value;
-(FMResultSet *)getRowEntity:(NSString *)tableName primaryKey:(NSString *)key;
-(NSMutableArray *)getReadedArticles;
-(NSMutableArray *)getNotReadArticles;
-(void)closeDB;
@end

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
-(void)createVersionTable;
-(void)createOperationTable;
-(void)createHistoryTable;
-(void)executeUpdate:(NSString *)sql;
-(void)insertArticleData:(NSMutableDictionary *)data;
-(FMResultSet *)getRowEntity:(NSString *)tableName primaryKey:(NSString *)key;
-(int)getVersionId;
-(NSMutableArray *)getReadedArticles;
-(NSMutableArray *)getNotReadArticles;
-(NSMutableArray *)getRecentArticles;
-(NSMutableArray *)getAllCategory;
-(NSMutableArray *) getAllImage:(NSString *)key;
-(void)closeDB;
@end

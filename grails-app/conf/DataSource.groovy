dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "entdb"
    password = "dm6db+1de@v"
	autoreconnect = true
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
	//内网 192.168.32.*，192.168.0.108
	//外网118.192.65.*
    development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://118.192.65.91:3306/ent_domain"
//			url = "jdbc:mysql://192.168.0.108:3306/ent_domain"

		}
		dataSource_ireport{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
			url = "jdbc:mysql://118.192.65.91:3306/ireport"
//			url = "jdbc:mysql://192.168.0.108:3306/ireport"
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		dataSource_crawl{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
			url = "jdbc:mysql://118.192.65.91:3306/ent_crawl"
//			url = "jdbc:mysql://192.168.0.108:3306/ent_crawl"
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}

		}
		
		//好评率cobar分表分库
		dataSource_cobar_reputation{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
			url = "jdbc:mysql://118.192.65.49:8066/iminer_index_partition"
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		
		//口碑相关mycat分表分库
		dataSource_mycat_reputation{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "IM*DT@19#!"
			url = "jdbc:mysql://192.168.32.116:8066/praise_partition"
			logSql = true
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		
		//iplay管理库
		dataSource_iplay{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
			url = "jdbc:mysql://118.192.65.91:3306/iplay"
//			url = "jdbc:mysql://192.168.32.194:3306/iplay"
//			url = "jdbc:mysql://192.168.0.108:3306/iplay"
			
			logSql = true
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		
		//iminer组织机构
		dataSource_operation{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
//			url = "jdbc:mysql://192.168.32.194:3306/operation"
			url = "jdbc:mysql://118.192.65.91:3306/operation"
//			url = "jdbc:mysql://192.168.0.108:3306/operation"
			
			logSql = true
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		
		
    }
    test {
        dataSource {
			
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://192.168.32.91:3306/ent_domain"
//			url = "jdbc:mysql://192.168.0.108:3306/ent_domain"
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}

		}
		dataSource_ireport{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
			url = "jdbc:mysql://192.168.32.91:3306/ireport"
//			url = "jdbc:mysql://192.168.0.108:3306/ireport"
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		dataSource_crawl{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
			url = "jdbc:mysql://192.168.32.91:3306/ent_crawl"
//			url = "jdbc:mysql://192.168.0.108:3306/ent_crawl"
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}

		}
		
		//好评率cobar分表分库
		dataSource_cobar_reputation{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
//			url = "jdbc:mysql://192.168.32.49:8066/praise_partition"
			url = "jdbc:mysql://192.168.32.49:8066/iminer_index_partition"
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		
		//口碑相关mycat分表分库
		dataSource_mycat_reputation{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "IM*DT@19#!"
			url = "jdbc:mysql://192.168.32.116:8066/praise_partition"
			logSql = true
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		//iplay管理库
		dataSource_iplay{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
//			url = "jdbc:mysql://192.168.32.194:3306/iplay"
			url = "jdbc:mysql://192.168.32.91:3306/iplay"
//			url = "jdbc:mysql://192.168.0.108:3306/iplay"
			logSql = true
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
		//iminer组织机构
		dataSource_operation{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "entdb"
			password = "dm6db+1de@v"
//			url = "jdbc:mysql://192.168.32.194:3306/operation"
			url = "jdbc:mysql://192.168.32.91:3306/operation"
//			url = "jdbc:mysql://192.168.0.108:3306/operation"
			logSql = true
			pooled = true
			properties {
				maxActive = -1
				maxIdle = 5
				minIdle = 2
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
				validationQuery = "SELECT 1"
			}
		}
    }
}

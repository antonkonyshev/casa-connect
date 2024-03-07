package name.antonkonyshev.home.di

import dagger.Module
import dagger.Provides
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.data.database.AppDatabase

@Module
class DatabaseModule {

    @Provides
    fun provideAppDatabase(): AppDatabase {
        return AppDatabase.instance(HomeApplication.instance)
    }
}
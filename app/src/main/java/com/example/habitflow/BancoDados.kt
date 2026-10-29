package com.example.habitflow

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity(tableName = "habitos")
data class HabitoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val categoria: String,
    val horario: String,
    val metaDias: Int
)

@Entity(tableName = "metas")
data class MetaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descricao: String,
    val concluida: Boolean = false
)

@Dao
interface HabitoDao {

    @Query("SELECT * FROM habitos")
    suspend fun buscarTodos(): List<HabitoEntity>

    @Query("SELECT * FROM habitos WHERE id = :id")
    suspend fun buscarPorId(id: Int): HabitoEntity?

    @Insert
    suspend fun inserir(habito: HabitoEntity): Long

    @Update
    suspend fun atualizar(habito: HabitoEntity): Int

    @Delete
    suspend fun deletar(habito: HabitoEntity): Int
}

@Dao
interface MetaDao {

    @Query("SELECT * FROM metas")
    suspend fun buscarTodos(): List<MetaEntity>

    @Query("SELECT * FROM metas WHERE id = :id")
    suspend fun buscarPorId(id: Int): MetaEntity?

    @Insert
    suspend fun inserir(meta: MetaEntity): Long

    @Update
    suspend fun atualizar(meta: MetaEntity): Int

    @Delete
    suspend fun deletar(meta: MetaEntity): Int
}

@Database(entities = [HabitoEntity::class, MetaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitoDao(): HabitoDao
    abstract fun metaDao(): MetaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun popularBancoSeVazio(db: AppDatabase) {
            if (db.habitoDao().buscarTodos().isEmpty()) {
                db.habitoDao().inserir(HabitoEntity(nome = "Beber 2L de Água", categoria = "Saúde", horario = "08:00", metaDias = 30))
                db.habitoDao().inserir(HabitoEntity(nome = "Exercício Físico", categoria = "Fitness", horario = "09:30", metaDias = 21))
                db.habitoDao().inserir(HabitoEntity(nome = "Ler 10 Páginas", categoria = "Estudos", horario = "20:00", metaDias = 14))
            }
            if (db.metaDao().buscarTodos().isEmpty()) {
                db.metaDao().inserir(MetaEntity(titulo = "Correr 5km sem parar", descricao = "Treinar 3 vezes por semana", concluida = false))
                db.metaDao().inserir(MetaEntity(titulo = "Ler 2 livros no mês", descricao = "Foco em desenvolvimento pessoal", concluida = true))
                db.metaDao().inserir(MetaEntity(titulo = "Meditar 10 min por dia", descricao = "Melhorar foco e ansiedade", concluida = false))
            }
        }
    }
}
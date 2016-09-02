package service

import models.Candidate
import play.api.libs.json.Json._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class CandidateService(val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext)
  extends MongoController
    with ReactiveMongoComponents {

  def collection: Future[JSONCollection] =
    database.map(_.collection[JSONCollection]("candidates"))

  def list: Future[Seq[Candidate]] =
    collection.flatMap{ _.find(obj())
      .cursor[Candidate](ReadPreference.primary)
      .collect[Seq]()
    }

  def insert(candidate: Candidate): Future[WriteResult] =
    collection.flatMap(_.insert(candidate))

  def find(candidateId: Long): Future[Option[Candidate]] =
    collection.flatMap {
      _.find(obj("_id" -> candidateId))
        .one[Candidate](ReadPreference.primary)
    }

}

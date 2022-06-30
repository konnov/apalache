package at.forsyte.apalache.tla.assignments.passes

import at.forsyte.apalache.infra.passes.Pass.PassResult
import at.forsyte.apalache.tla.lir._
import at.forsyte.apalache.io.lir.TlaWriterFactory
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import at.forsyte.apalache.tla.imp.src.SourceStore
import at.forsyte.apalache.tla.lir.storage.ChangeListener
import at.forsyte.apalache.tla.assignments.EnabledRewriter
import at.forsyte.apalache.tla.lir.transformations.TransformationTracker
import at.forsyte.apalache.tla.pp.Inliner
import at.forsyte.apalache.tla.lir.transformations.standard.IncrementalRenaming

/**
 * Rewrites ENABLED conditions
 */
class EnabledRewriterPassImpl @Inject() (
    tracker: TransformationTracker,
    writerFactory: TlaWriterFactory,
    sourceStore: SourceStore,
    changeListener: ChangeListener,
    renaming: IncrementalRenaming)
    extends EnabledRewriterPass with LazyLogging {

  override def name: String = "EnabledRewriterPass"

  override def execute(tlaModule: TlaModule): PassResult = {
    val enabledRewriter = new EnabledRewriter(tracker, sourceStore, changeListener, tlaModule)
    val inliner = new Inliner(tracker, renaming, keepNullaryMono = false)

    // EnabledRewriter relies on LET-IN freedom and total inlining, even of nullary operators
    val inlinedModule = inliner.transformModule(tlaModule)

    val newModule = inlinedModule.copy(
        declarations = inlinedModule.declarations.map {
          case d: TlaOperDecl => d.copy(body = enabledRewriter(d.body))
          case d              => d
        }
    )

    writeOut(writerFactory, newModule)

    Right(newModule)
  }

  override def dependencies = Set(ModuleProperty.Configured, ModuleProperty.Inlined)

  override def transformations = Set(ModuleProperty.EnabledRewritten)
}

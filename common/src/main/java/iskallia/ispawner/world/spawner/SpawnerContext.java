package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;

public class SpawnerContext {

    private final SpawnerExecution execution;
    private final SpawnerBlockEntity entity;

    public SpawnerContext(SpawnerExecution execution, SpawnerBlockEntity entity) {
        this.execution = execution;
        this.entity = entity;
    }

    public SpawnerExecution getExecution() {
        return this.execution;
    }

    public SpawnerBlockEntity getEntity() {
        return this.entity;
    }

}
